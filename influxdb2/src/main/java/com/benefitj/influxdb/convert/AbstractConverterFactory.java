package com.benefitj.influxdb.convert;

import com.benefitj.core.ReflectUtils;
import com.benefitj.influxdb.InfluxUtils;
import com.benefitj.influxdb.annotation.Column;
import com.benefitj.influxdb.annotation.Measurement;
import com.benefitj.influxdb.annotation.TimeColumn;
import com.benefitj.influxdb.annotation.ColumnIgnore;
import com.benefitj.influxdb.dto.QueryResult;
import com.benefitj.influxdb.annotation.TagNullable;

import java.lang.reflect.Field;
import java.util.*;
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
    converter.setMeasurement(InfluxUtils.isNotBlank(measurement.name())
        ? measurement.name() : type.getSimpleName());

    // 忽略被static和final修饰的字段、忽略InfluxIgnore注解的字段
    final Map<String, Field> fields = new LinkedHashMap<>();
    ReflectUtils.findFields(type
        // 不是static/final，没有被ColumnIgnore注释
        , f -> !(ReflectUtils.isStaticOrFinal(f) || f.isAnnotationPresent(ColumnIgnore.class))
        , f -> fields.putIfAbsent(f.getName(), f)
        , f -> false
    );

    if (fields.isEmpty()) {
      throw new IllegalArgumentException("The columns is empty!");
    }

    // 时间戳单位
    converter.setTimestampUnit(measurement.timeUnit());
    // 解析字段
    for (Field field : fields.values()) {
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
      columnField.setColumn(InfluxUtils.isNotBlank(column.name()) ? column.name() : field.getName());
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
