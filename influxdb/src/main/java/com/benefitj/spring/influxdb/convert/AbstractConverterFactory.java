package com.benefitj.spring.influxdb.convert;

import com.benefitj.spring.influxdb.InfluxDBUtils;
import com.benefitj.spring.influxdb.ReflectUtils;
import com.benefitj.spring.influxdb.dto.ColumnIgnore;
import com.benefitj.spring.influxdb.dto.TagNullable;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;
import org.influxdb.dto.QueryResult;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 转换器工厂
 */
public abstract class AbstractConverterFactory<U> implements ConverterFactory<U> {

  /**
   * PoJo 转换类
   */
  private final InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

  public AbstractConverterFactory() {
  }

  @Override
  public abstract <T> Converter<T, U> getConverter(Class<T> type);

  /**
   * 转换对象
   *
   * @param record 对象
   * @return 返回转换后的对象
   */
  @Override
  public <T> U convert(T record) {
    return getConverter((Class<T>) record.getClass()).convert(record);
  }

  @Override
  public <T> List<U> convert(Collection<T> records) {
    return records.stream()
        .filter(Objects::nonNull)
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public InfluxDBResultMapper getResultMapper() {
    return resultMapper;
  }

  @Override
  public <T> List<T> mapperTo(QueryResult result, Class<T> type) {
    return getResultMapper().toPOJO(result, type);
  }

  /**
   * 解析转换器
   *
   * @param converter 转换器
   * @param <T>       原对象
   * @param <C>       转换器
   */
  public <T, C extends AbstractConverter<T, U>> void parse(C converter) {
    final Class<T> type = converter.getType();
    if (!type.isAnnotationPresent(Measurement.class)) {
      throw new IllegalStateException("\"" + type + "\"没有被\"org.influxdb.annotation.Measurement\"注解注释!");
    }

    Measurement measurement = type.getAnnotation(Measurement.class);
    converter.setMeasurement(InfluxDBUtils.isNotBlank(measurement.name())
        ? measurement.name() : type.getSimpleName());

    // 忽略被static和final修饰的字段、忽略InfluxIgnore注解的字段
    Map<String, Field> fieldMap = ReflectUtils.getFields(type, f ->
        !(ReflectUtils.isStaticOrFinal(f.getModifiers()) || f.isAnnotationPresent(ColumnIgnore.class)));

    if (fieldMap.isEmpty()) {
      throw new IllegalArgumentException("The columns is empty!");
    }

    // 时间戳单位
    converter.setTimestampUnit(measurement.timeUnit());
    // 解析字段
    for (Field field : fieldMap.values()) {
      final ColumnField columnField = parseColumn(field);
      if (isTimestamp(field)) {
        converter.setTimestamp(columnField);
      } else {
        if (columnField.isTag()) {
          converter.putTag(columnField);
        } else {
          converter.putColumn(columnField);
        }
      }
    }
  }

  /**
   * 解析字段
   */
  public ColumnField parseColumn(Field field) {
    return parseColumn(new ColumnField(field), isTimestamp(field));
  }

  /**
   * 解析字段
   */
  public ColumnField parseColumn(ColumnField columnField, boolean timestamp) {
    Field field = columnField.getField();
    Column column = field.getAnnotation(Column.class);
    if (column != null) {
      columnField.setColumn(InfluxDBUtils.isNotBlank(column.name()) ? column.name() : field.getName());
      if (timestamp) {
        columnField.setTagNullable(false);
      } else {
        columnField.setTag(column.tag());
        if (column.tag()) {
          if (field.getType() != String.class) {
            throw new IllegalStateException("InfluxDB中tag只能为java.lang.String类型, \""
                + column.name() + "\"的类型为\"" + field.getType() + "\"");
          }
          // 设置TAG是否允许为 null
          TagNullable tagNullable = field.getAnnotation(TagNullable.class);
          if (tagNullable != null) {
            columnField.setTagNullable(tagNullable.value());
          } else {
            columnField.setTagNullable(false);
          }
        }
      }
    } else {
      columnField.setColumn(field.getName());
      columnField.setTag(false);
    }
    return columnField;
  }

  /**
   * 判断是否为时间戳
   *
   * @param field 字段
   * @return 返回是否为时间戳的判断
   */
  public boolean isTimestamp(Field field) {
    return field.isAnnotationPresent(TimeColumn.class);
  }

}
