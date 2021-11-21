package com.benefitj.spring.influxdb.convert;

import com.benefitj.core.ReflectUtils;
import org.influxdb.dto.Point;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 抽象的转换器
 *
 * @param <T> 原对象
 * @param <U> 目标对象
 */
public abstract class AbstractConverter<T, U> implements Converter<T, U> {

  /**
   * type
   */
  private final Class<T> type;
  /**
   * measurement
   */
  private String measurement;
  /**
   * columns
   */
  private final Map<String, ColumnField> columns = new ConcurrentHashMap<>();
  /**
   * tags
   */
  private final Map<String, ColumnField> tags = new ConcurrentHashMap<>();
  /**
   * 时间戳字段
   */
  private ColumnField timestamp;
  /**
   * 时间戳的单位，默认毫秒
   */
  private TimeUnit timestampUnit = TimeUnit.MILLISECONDS;

  public AbstractConverter(Class<T> type) {
    this.type = type;
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  @Override
  public String getMeasurement() {
    return measurement;
  }

  public void setMeasurement(String measurement) {
    this.measurement = measurement;
  }

  @Override
  public Map<String, ColumnField> getColumns() {
    return columns;
  }

  public void putColumns(Map<String, ColumnField> columns) {
    this.columns.putAll(columns);
  }

  @Override
  public Map<String, ColumnField> getTags() {
    return tags;
  }

  public void putTags(Map<String, ColumnField> tags) {
    this.tags.putAll(tags);
  }

  @Override
  public ColumnField getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(ColumnField timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public void setTimestampUnit(TimeUnit timestampUnit) {
    this.timestampUnit = timestampUnit;
  }

  @Override
  public TimeUnit getTimestampUnit() {
    return timestampUnit;
  }

  public ColumnField getColumn(String name) {
    return getColumns().get(name);
  }

  public ColumnField getTag(String name) {
    return getTags().get(name);
  }

  public void putColumn(ColumnField property) {
    putColumn(property.getColumn(), property);
  }

  public void putColumn(String name, ColumnField property) {
    this.columns.put(name, property);
  }

  public void putTag(ColumnField property) {
    putTag(property.getColumn(), property);
  }

  public void putTag(String name, ColumnField property) {
    this.tags.put(name, property);
  }


  /**
   * time method
   */
  private static final AtomicReference<Method> TIME_METHOD = new AtomicReference<>(null);

  /**
   * 转换成 Point
   *
   * @param converter 转换器
   * @param item      对象
   * @param <T>       对象的类型
   * @return 返回转换后的Point
   */
  public static <T> Point convert(AbstractConverter<T, ?> converter, T item) {
    if (item instanceof Point) {
      return (Point) item;
    }

    final Point.Builder builder = Point.measurement(converter.getMeasurement());
    // 设置时间戳
    Long timestamp = getTimestamp(converter.getTimestamp(), item);
    if (TIME_METHOD.get() != null) {
      try {
        TIME_METHOD.get().invoke(builder, timestamp, converter.getTimestampUnit());
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalStateException(e);
      }
    } else {
      try {
        builder.time(timestamp, converter.getTimestampUnit());
      } catch (NoSuchMethodError e) {
        try {
          Method method = builder.getClass().getDeclaredMethod("time", long.class, TimeUnit.class);
          TIME_METHOD.set(method);
          method.invoke(builder, timestamp, converter.getTimestampUnit());
        } catch (Exception ee) {
          throw new IllegalStateException(ee);
        }
      }
    }

    // TAG
    final Map<String, ColumnField> tags = converter.getTags();
    tags.forEach((tag, columnField) -> {
      Object value = ReflectUtils.getFieldValue(columnField.getField(), item);
      // 检查是否允许tag为null，默认不允许
      if (value == null && !columnField.isTagNullable()) {
        throw new NullPointerException("tag is null.");
      }
      if (value != null) {
        builder.tag(tag, (String) value);
      }
      // ~ 忽略值为null的tag
    });

    // column
    final Map<String, ColumnField> columns = converter.getColumns();
    columns.forEach((name, columnField) -> {
      Object value = ReflectUtils.getFieldValue(columnField.getField(), item);
      if (value instanceof Number) {
        builder.addField(name, (Number) value);
      } else if (value instanceof Boolean) {
        builder.addField(name, (Boolean) value);
      } else if (value instanceof String) {
        builder.addField(name, (String) value);
      } else {
        if (value != null) {
          builder.addField(name, String.valueOf(value));
        }
        // ~ 忽略值为null的字段
      }
    });
    return builder.build();
  }

  /**
   * 获取对象的时间戳
   */
  protected static long getTimestamp(ColumnField columnField, Object item) {
    // 设置时间戳
    Object value = ReflectUtils.getFieldValue(columnField.getField(), item);
    if (value instanceof Long) {
      return (Long) value;
    } else if (value instanceof Date) {
      return ((Date) value).getTime();
    } else {
      throw new NullPointerException("timestamp");
    }
  }

}
