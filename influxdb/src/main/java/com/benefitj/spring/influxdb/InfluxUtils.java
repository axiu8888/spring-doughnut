package com.benefitj.spring.influxdb;


import com.benefitj.core.ReflectUtils;
import com.benefitj.spring.influxdb.annotation.Column;
import com.benefitj.spring.influxdb.annotation.ColumnIgnore;
import com.benefitj.spring.influxdb.convert.LineProtocolConverter;
import com.benefitj.spring.influxdb.convert.LineProtocolConverterFactory;
import com.benefitj.spring.influxdb.convert.PointConverter;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * InfluxDB工具
 */
public class InfluxUtils {

  /**
   * point 转换器工厂
   */
  private static final PointConverterFactory POINT_FACTORY = PointConverterFactory.INSTANCE;
  /**
   * line protocol 转换器工厂
   */
  private static final LineProtocolConverterFactory LINE_FACTORY = LineProtocolConverterFactory.INSTANCE;

  /**
   * 获取point转换器
   *
   * @param type 类型
   * @param <T>  原对象类型
   * @return 返回行协议转换器
   */
  public static <T> PointConverter<T> getPointConverter(Class<T> type) {
    return POINT_FACTORY.getConverter(type);
  }

  /**
   * 获取行协议转换器
   *
   * @param type 类型
   * @param <T>  原对象类型
   * @return 返回行协议转换器
   */
  public static <T> LineProtocolConverter<T> getLineProtocolConverter(Class<T> type) {
    return LINE_FACTORY.getConverter(type);
  }

  /**
   * 转换成行协议数据
   *
   * @param item 原对象
   * @return 返回 line protocol String
   */
  @SuppressWarnings("all")
  static String convert(Object item) {
    LineProtocolConverter converter = getLineProtocolConverter(item.getClass());
    return converter.convert(item);
  }

  /**
   * 转换成对象
   *
   * @param type 对象类型
   * @param line 行协议
   * @param <T>  对象类型
   * @return 返回转换的对象
   */
  public static <T> T toPoJo(Class<T> type, LineProtocol line) {
    try {
      return toPoJo(type.newInstance(), line);
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 转换成对象
   *
   * @param target 目标对象
   * @param line   行协议
   * @param <T>    对象类型
   * @return 返回转换的对象
   */
  public static <T> T toPoJo(T target, LineProtocol line) {
    Map<String, String> tags = line.getTags();
    Map<String, Object> fields = line.getFields();
    ReflectUtils.findFields(target.getClass()
        , f -> !f.isAnnotationPresent(ColumnIgnore.class)
        , f -> {
          if (f.isAnnotationPresent(Column.class)) {
            Column column = f.getAnnotation(Column.class);
            if (column.tag()) {
              ReflectUtils.setFieldValue(f, target, tags.get(column.name()));
            } else {
              Object value = fields.get(column.name());
              if (value instanceof Number) {
                Class<?> fieldOfType = ReflectUtils.getFieldOfType(f, target);
                if (fieldOfType == Byte.class || fieldOfType == byte.class) {
                  ReflectUtils.setFieldValue(f, target, ((Number) value).byteValue());
                } else if (fieldOfType == Short.class || fieldOfType == short.class) {
                  ReflectUtils.setFieldValue(f, target, ((Number) value).shortValue());
                } else if (fieldOfType == Integer.class || fieldOfType == int.class) {
                  ReflectUtils.setFieldValue(f, target, ((Number) value).intValue());
                } else if (fieldOfType == Long.class || fieldOfType == long.class) {
                  ReflectUtils.setFieldValue(f, target, ((Number) value).longValue());
                } else if (fieldOfType == Float.class || fieldOfType == float.class) {
                  ReflectUtils.setFieldValue(f, target, ((Number) value).floatValue());
                } else if (fieldOfType == Double.class || fieldOfType == double.class) {
                  ReflectUtils.setFieldValue(f, target, ((Number) value).doubleValue());
                }
              } else {
                ReflectUtils.setFieldValue(f, target, value);
              }
            }
          } else {
            ReflectUtils.setFieldValue(f, target, fields.get(f.getName()));
          }
        }
        , f -> false);
    return target;
  }

  /**
   * 转换成行协议数据
   *
   * @param payload 原对象
   * @return 返回 line protocol String
   */
  @SuppressWarnings("all")
  public static List<String> toLineProtocol(Object payload) {
    if (payload instanceof CharSequence) {
      return Collections.singletonList(payload.toString());
    } else if (payload instanceof Point) {
      return Collections.singletonList(((Point) payload).lineProtocol());
    } else if (payload instanceof Collection) {
      return toLineProtocol((Collection) payload);
    } else {
      return Collections.singletonList(convert(payload));
    }
  }

  /**
   * 转换成行协议数据
   *
   * @param items 目标对象集合
   * @return 返回 line protocol String
   */
  public static List<String> toLineProtocol(Collection<?> items) {
    return items.stream()
        .map(InfluxUtils::convert)
        .collect(Collectors.toList());
  }

  /**
   * 转换成行协议数据
   *
   * @param points Point list
   * @return 返回 line protocol string
   */
  public static String toLineProtocol(List<Point> points) {
    return toLineProtocol(points, null);
  }

  /**
   * 转换成行协议数据
   *
   * @param points    Point list
   * @param precision 时间片单位
   * @return 返回 line protocol string
   */
  public static String toLineProtocol(List<Point> points, TimeUnit precision) {
    final BatchPoints ops = BatchPoints.database(null)
        .precision(precision)
        .build();
    points.forEach(ops::point);
    return ops.lineProtocol();
  }

  /**
   * 转换成 Point
   *
   * @param protocol 行协议
   * @return return Point
   */
  public static Point.Builder toPointBuilder(LineProtocol protocol) {
    Point.Builder builder = new Point.Builder(protocol.getMeasurement());
    builder.time(protocol.getTime(), TimeUnit.NANOSECONDS);
    builder.tag(protocol.getTags());
    builder.fields(protocol.getFields());
    return builder;
  }

  /**
   * 转换成 Point
   *
   * @param protocol 行协议
   * @return return Point
   */
  public static Point toPoint(LineProtocol protocol) {
    return toPointBuilder(protocol).build();
  }

  /**
   * 转换成 Point
   *
   * @param result    query result
   * @param fieldKeys field keys
   * @return return Point
   */
  public static List<Point> toPoint(QueryResult result, Map<String, FieldKey> fieldKeys) {
    return result.getResults()
        .stream()
        .flatMap(r -> {
          List<QueryResult.Series> series = r.getSeries();
          return series != null ? series.stream() : Stream.empty();
        })
        .flatMap(series -> {
          fieldKeys.forEach((name, field) -> field.setIndex(-1)); // // 重置
          List<String> columns = series.getColumns();
          String column;
          for (int i = 0; i < columns.size(); i++) {
            column = columns.get(i);
            final FieldKey fieldKey = fieldKeys.get(column);
            if (fieldKey == null) {
              throw new IllegalArgumentException("Not found column[\"" + column + "\"]");
            }
            fieldKey.setIndex(i);
          }
          final List<Point> points = new LinkedList<>();
          List<List<Object>> values = series.getValues();
          for (List<Object> value : values) {
            points.add(toPoint(series.getName(), fieldKeys, value));
          }
          return points.stream();
        })
        .collect(Collectors.toList());
  }

  /**
   * 转换成 Point
   *
   * @param measurement MEASUREMENT
   * @param fieldKeys   field keys
   * @param values      values
   * @return return Point
   */
  public static Point toPoint(String measurement, Map<String, FieldKey> fieldKeys, List<Object> values) {
    final Point.Builder builder = Point.measurement(measurement);
    for (FieldKey fieldKey : fieldKeys.values()) {
      if (fieldKey.getIndex() >= 0) {
        String column = fieldKey.getColumn();
        Object value = values.get(fieldKey.getIndex());
        if (fieldKey.isTimestamp()) {
          if (value instanceof Number) {
            builder.time(((Number) value).longValue(), TimeUnit.NANOSECONDS);
          } else {
            builder.time(InfluxTimeUtil.fromInfluxDBTimeFormat((String) value), TimeUnit.MILLISECONDS);
          }
        } else {
          if (fieldKey.isTag()) {
            if (value != null) {
              builder.tag(column, (String) value);
            }
          } else {
            if (value instanceof Number) {
              builder.addField(column, fieldKey.getNumber(value));
            } else if (value instanceof CharSequence) {
              builder.addField(column, value.toString());
            } else if (value instanceof Boolean) {
              builder.addField(column, (Boolean) value);
            }
          }
        }
      }
    }
    return builder.build();
  }

  /**
   * 解析行协议
   *
   * @param line 数据行
   * @return 返回行协议对象
   */
  public static LineProtocol parseLine(String line) {
    if (InfluxUtils.isBlank(line)) {
      throw new IllegalArgumentException("数据不能为空");
    }
    LineProtocol lineProtocol = new LineProtocol();
    lineProtocol.setTags(new LinkedHashMap<>());
    lineProtocol.setFields(new LinkedHashMap<>());
    // 引号
    boolean hasSingleQuote = false, hasDoubleQuote = false, escape = false;
    int stage = 0;
    for (int i = 0, startAt = 0; i < line.length(); i++) {
      char ch = line.charAt(i);
      if (escape || ch == '\\') {
        // 有转义符，跳过
        escape = !escape;
      } else {
        if (hasSingleQuote || hasDoubleQuote) {
          // 有引号，需要退出引号后记录
          if (hasSingleQuote && ch == '/') {
            hasSingleQuote = false;
          }
          if (hasDoubleQuote && ch == '"') {
            hasDoubleQuote = false;
          }
        } else {
          if (ch == ' ' || ch == ',') {
            // 0 =>: tag和字段的分割
            switch (stage) {
              case 0:
                lineProtocol.setMeasurement(line.substring(startAt, i));
                stage++;// tag
                break;
              case 1: {
                String str = line.substring(startAt, i);
                if (InfluxUtils.isNotBlank(str)) {
                  String[] splits = str.split("=");
                  lineProtocol.getTags().put(splits[0], splits[1]);
                }
              }
              break;
              case 2: {
                String str = line.substring(startAt, i);
                String[] splits = str.split("=");
                lineProtocol.getFields().put(splits[0], parseValue(splits[1]));
              }
              break;
              case 3:
                lineProtocol.setTime(Long.parseLong(line.substring(startAt)));
                break;
            }
            stage = stage + (ch == ' ' ? 1 : 0);
            startAt = i + 1;
          } else {
            if (ch == '\'') {
              hasSingleQuote = true;
            }
            if (ch == '"') {
              hasDoubleQuote = true;
            }
          }
        }
      }
    }
    int spaceLastIndexOf = line.lastIndexOf(" ");
    if (spaceLastIndexOf >= 0) {
      lineProtocol.setTime(Long.parseLong(line.substring(spaceLastIndexOf).trim()));
    } else {
      throw new IllegalStateException("缺少时间戳");
    }
    return lineProtocol;
  }

  /**
   * 解析值
   *
   * @param value 值
   * @return 返回解析的数据
   */
  public static Object parseValue(String value) {
    if (value.startsWith("\"") && value.endsWith("\"")) {
      return value.substring(1, value.length() - 1);
    }
    if (value.endsWith("i")) {
      return Long.parseLong(value.substring(0, value.length() - 1));
    }
    switch (value) {
      case "false":
      case "true":
        return Boolean.parseBoolean(value);
      default:
        return Double.parseDouble(value.substring(0, value.length() - 1));
    }
  }

  /**
   * Encode a command into {@code x-www-form-urlencoded} format.
   *
   * @param command the command to be encoded.
   * @return a encoded command.
   */
  public static String encode(final String command) {
    try {
      return URLEncoder.encode(command, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Every JRE must support UTF-8", e);
    }
  }


  /**
   * Encode a command into {@code x-www-form-urlencoded} format.
   *
   * @param command the command to be encoded.
   * @return a encoded command.
   */
  public static String encodeWithURL(final String command) {
    try {
      return URLEncoder.encode(command, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Every JRE must support UTF-8", e);
    }
  }

  /**
   * 是否不为空
   */
  public static boolean isNoneBlank(final CharSequence... cs) {
    for (CharSequence c : cs) {
      if (isBlank(c)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 是否不为空
   */
  public static boolean isNotBlank(final CharSequence cs) {
    return !isBlank(cs);
  }

  /**
   * 是否为空
   */
  public static boolean isBlank(final CharSequence cs) {
    int strLen;
    if (cs == null || (strLen = cs.length()) == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

}
