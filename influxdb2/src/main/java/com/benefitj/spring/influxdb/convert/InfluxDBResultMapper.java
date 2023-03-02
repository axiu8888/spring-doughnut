package com.benefitj.spring.influxdb.convert;


import com.benefitj.core.ReflectUtils;
import com.benefitj.spring.influxdb.InfluxException;
import com.benefitj.spring.influxdb.InfluxTimeUtil;
import com.benefitj.spring.influxdb.annotation.Column;
import com.benefitj.spring.influxdb.annotation.Measurement;
import com.benefitj.spring.influxdb.annotation.TimeColumn;
import com.benefitj.spring.influxdb.dto.QueryResult;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Main class responsible for mapping a QueryResult to a POJO.
 *
 * @author fmachado
 */
public class InfluxDBResultMapper {

  /**
   * Data structure used to cache classes used as measurements.
   */
  private static final Map<String, Map<String, Field>> CLASS_FIELD_CACHE = new ConcurrentHashMap<>();

  private static final int FRACTION_MIN_WIDTH = 0;
  private static final int FRACTION_MAX_WIDTH = 9;
  private static final boolean ADD_DECIMAL_POINT = true;

  /**
   * When a query is executed without {@link TimeUnit}, InfluxDB returns the <tt>time</tt>
   * column as an ISO8601 date.
   */
  private static final DateTimeFormatter ISO8601_FORMATTER = new DateTimeFormatterBuilder()
          .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
          .appendFraction(ChronoField.NANO_OF_SECOND, FRACTION_MIN_WIDTH, FRACTION_MAX_WIDTH, ADD_DECIMAL_POINT)
          .appendPattern("X")
          .toFormatter();

  public static Map<String, Map<String, Field>> getClassFieldCache() {
    return CLASS_FIELD_CACHE;
  }

  /**
   * 时间戳字段
   */
  private String timestamp = "time";

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * <p>
   * Process a {@link QueryResult} object returned by the InfluxDB client inspecting the internal
   * data structure and creating the respective object instances based on the Class passed as
   * parameter.
   * </p>
   *
   * @param queryResult the InfluxDB result object
   * @param clazz       the Class that will be used to hold your measurement data
   * @param <T>         the target type
   * @return a {@link List} of objects from the same Class passed as parameter and sorted on the
   * same order as received from InfluxDB.
   * @throws InfluxException If {@link QueryResult} parameter contain errors,
   *                                 <tt>clazz</tt> parameter is not annotated with &#64;Measurement or it was not
   *                                 possible to define the values of your POJO (e.g. due to an unsupported field type).
   */
  public <T> List<T> toPOJO(final QueryResult queryResult, final Class<T> clazz) throws InfluxException {
    throwExceptionIfMissingAnnotation(clazz);
    Objects.requireNonNull(queryResult, "queryResult");
    Objects.requireNonNull(clazz, "clazz");

    throwExceptionIfResultWithError(queryResult);
    cacheMeasurementClass(clazz);

    final List<T> result = new LinkedList<>();
    queryResult.getResults()
            .stream()
            .filter(qr -> (qr != null) && qr.getSeries() != null)
            .forEach(qr -> qr.getSeries().forEach(qs -> parseSeriesAs(qs, clazz, result)));
    return result;
  }

  static void throwExceptionIfMissingAnnotation(final Class<?> clazz) {
    if (!clazz.isAnnotationPresent(Measurement.class)) {
      throw new IllegalArgumentException(
              "Class " + clazz.getName() + " is not annotated with @" + Measurement.class.getSimpleName());
    }
  }

  static void throwExceptionIfResultWithError(final QueryResult queryResult) {
    if (queryResult.getError() != null) {
      throw new InfluxException("InfluxDB returned an error: " + queryResult.getError());
    }

    queryResult.getResults().forEach(seriesResult -> {
      if (seriesResult.getError() != null) {
        throw new InfluxException("InfluxDB returned an error with Series: " + seriesResult.getError());
      }
    });
  }

  protected void cacheMeasurementClass(final Class<?>... classVarAgrs) {
    for (Class<?> clazz : classVarAgrs) {
      if (getClassFieldCache().containsKey(clazz.getName())) {
        continue;
      }

      final Map<String, Field> fields = ReflectUtils.getFieldMap(clazz, f -> !ReflectUtils.isStaticOrFinal(f.getModifiers()));
      final Map<String, Field> columnFieldMap = new ConcurrentHashMap<>(fields.size());
      fields.forEach((fieldName, field) -> {
        if (field.isAnnotationPresent(Column.class)) {
          columnFieldMap.put(field.getAnnotation(Column.class).name(), field);
        } else {
          columnFieldMap.put(field.getName(), field);
        }
      });
      getClassFieldCache().put(clazz.getName(), columnFieldMap);
    }
  }

  protected static String getMeasurementName(final Class<?> clazz) {
    return clazz.getAnnotation(Measurement.class).name();
  }

  protected <T> List<T> parseSeriesAs(final QueryResult.Series series, final Class<T> clazz, final List<T> result) {
    int columnSize = series.getColumns().size();
    Map<String, Field> colNameAndFieldMap = getClassFieldCache().get(clazz.getName());
    try {
      Measurement measurement = clazz.getAnnotation(Measurement.class);
      T object = null;
      for (List<Object> row : series.getValues()) {
        for (int i = 0; i < columnSize; i++) {
          Field correspondingField = colNameAndFieldMap.get(series.getColumns().get(i)/*InfluxDB columnName*/);
          if (correspondingField != null) {
            if (object == null) {
              object = clazz.newInstance();
            }

            // 设置时间戳
            if (timestamp.equals(correspondingField.getName())) {
              Object value = row.get(i);

              long time;
              if (value instanceof Number) {
                TimeColumn timeColumn = correspondingField.getAnnotation(TimeColumn.class);
                long nanos = (timeColumn != null ? timeColumn.timeUnit() : measurement.timeUnit()).toNanos(1);
                time = ((Number) value).longValue() / nanos;
              } else {
                time = InfluxTimeUtil.fromInfluxDBTimeFormat(value.toString());
              }
              if (correspondingField.getGenericType() instanceof Date) {
                value = new Date(time);
              } else {
                value = time;
              }
              setFieldValue(object, correspondingField, value);
              continue;
            }

            setFieldValue(object, correspondingField, row.get(i));
          }
        }
        // When the "GROUP BY" clause is used, "tags" are returned as Map<String,String> and
        // accordingly with InfluxDB documentation
        // https://docs.influxdata.com/influxdb/v1.2/concepts/glossary/#tag-value
        // "tag" values are always String.
        if (series.getTags() != null && !series.getTags().isEmpty()) {
          for (Entry<String, String> entry : series.getTags().entrySet()) {
            Field correspondingField = colNameAndFieldMap.get(entry.getKey()/*InfluxDB columnName*/);
            if (correspondingField != null) {
              // I don't think it is possible to reach here without a valid "object"
              setFieldValue(object, correspondingField, entry.getValue());
            }
          }
        }
        if (object != null) {
          result.add(object);
          object = null;
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      throw new InfluxException(e);
    }
    return result;
  }

  /**
   * InfluxDB client returns any number as Double.
   * See https://github.com/influxdata/influxdb-java/issues/153#issuecomment-259681987
   * for more information.
   *
   * @param object
   * @param field
   * @param value
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  public static <T> void setFieldValue(final T object, final Field field, Object value)
          throws IllegalArgumentException, IllegalAccessException {
    if (value == null) {
      return;
    }
    Class<?> fieldType = field.getType();
    try {

      // 设置普通字段

      if (!field.isAccessible()) {
        field.setAccessible(true);
      }

      // 泛型字段
      if (field.getGenericType() instanceof TypeVariable) {
        // TODO: 2021/11/20 待验证
        fieldType = (Class<?>) ((TypeVariable) field.getGenericType()).getBounds()[0];
      }

      if (fieldValueModified(fieldType, field, object, value)
              || fieldValueForPrimitivesModified(fieldType, field, object, value)
              || fieldValueForPrimitiveWrappersModified(fieldType, field, object, value)) {
        return;
      }

      String msg = "Class '%s' field '%s' is from an unsupported type '%s'.";
      throw new InfluxException(
              String.format(msg, object.getClass().getName(), field.getName(), field.getType()));
    } catch (ClassCastException e) {
      e.printStackTrace();

      String msg = "Class '%s' field '%s' was defined with a different field type and caused a ClassCastException. "
              + "The correct type is '%s' (current field value: '%s').";
      throw new InfluxException(
              String.format(msg, object.getClass().getName(), field.getName(), value.getClass().getName(), value));
    }
  }

  public static <T> boolean fieldValueModified(final Class<?> fieldType, final Field field, final T object, final Object value)
          throws IllegalArgumentException, IllegalAccessException {
    if (String.class.isAssignableFrom(fieldType)) {
      field.set(object, String.valueOf(value));
      return true;
    }
    if (Instant.class.isAssignableFrom(fieldType)) {
      Instant instant;
      if (value instanceof String) {
        instant = Instant.from(ISO8601_FORMATTER.parse(String.valueOf(value)));
      } else if (value instanceof Long) {
        instant = Instant.ofEpochMilli((Long) value);
      } else if (value instanceof Double) {
        instant = Instant.ofEpochMilli(((Double) value).longValue());
      } else {
        throw new InfluxException("Unsupported type " + field.getClass() + " for field " + field.getName());
      }
      field.set(object, instant);
      return true;
    }
    return false;
  }

  public static <T> boolean fieldValueForPrimitivesModified(final Class<?> fieldType,
                                                            final Field field,
                                                            final T object,
                                                            final Object value)
          throws IllegalArgumentException, IllegalAccessException {

    if (value instanceof Number) {
      Number number = (Number) value;
      if (double.class.isAssignableFrom(fieldType)) {
        field.setDouble(object, number.doubleValue());
        return true;
      } else if (float.class.isAssignableFrom(fieldType)) {
        field.setFloat(object, number.floatValue());
        return true;
      } else if (long.class.isAssignableFrom(fieldType)) {
        field.setLong(object, number.longValue());
        return true;
      } else if (int.class.isAssignableFrom(fieldType)) {
        field.setInt(object, number.intValue());
        return true;
      } else if (short.class.isAssignableFrom(fieldType)) {
        field.setShort(object, number.shortValue());
        return true;
      } else if (byte.class.isAssignableFrom(fieldType)) {
        field.setByte(object, number.byteValue());
        return true;
      }
    } else if (boolean.class.isAssignableFrom(fieldType)) {
      field.setBoolean(object, Boolean.parseBoolean(String.valueOf(value)));
      return true;
    } else if (value instanceof CharSequence) {
      if (boolean.class.isAssignableFrom(fieldType)) {
        field.setBoolean(object, Boolean.parseBoolean(String.valueOf(value)));
        return true;
      }
    }
    return false;
  }

  public static <T> boolean fieldValueForPrimitiveWrappersModified(final Class<?> fieldType,
                                                                   final Field field,
                                                                   final T object,
                                                                   final Object value)
          throws IllegalArgumentException, IllegalAccessException {

    Object tmpValue = null;
    if (value instanceof Number) {
      Number number = (Number) value;
      if (Double.class.isAssignableFrom(fieldType)) {
        tmpValue = value;
      } else if (Float.class.isAssignableFrom(fieldType)) {
        tmpValue = number.floatValue();
      } else if (Long.class.isAssignableFrom(fieldType)) {
        tmpValue = number.longValue();
      } else if (Integer.class.isAssignableFrom(fieldType)) {
        tmpValue = number.intValue();
      } else if (Short.class.isAssignableFrom(fieldType)) {
        tmpValue = number.shortValue();
      } else if (Byte.class.isAssignableFrom(fieldType)) {
        tmpValue = number.byteValue();
      }
    } else if (Boolean.class.isAssignableFrom(fieldType)) {
      tmpValue = Boolean.parseBoolean(String.valueOf(value));
    } else if (value instanceof CharSequence) {
      if (Boolean.class.isAssignableFrom(fieldType)) {
        tmpValue = Boolean.valueOf(String.valueOf(value));
      }
    }

    if (tmpValue != null) {
      field.set(object, tmpValue);
      return true;
    }
    return false;
  }
}

