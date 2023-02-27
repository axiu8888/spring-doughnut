package com.benefitj.influxdb.template;

import com.benefitj.influxdb.InfluxTimeUtil;
import com.benefitj.influxdb.dto.QueryResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数值转换
 */
public interface ValueConverter {

  String TIME = "time";

  /**
   * 设置序列
   *
   * @param series 序列
   * @return 返回值转换器
   */
  ValueConverter setSeries(QueryResult.Series series);

  /**
   * 获取序列
   */
  QueryResult.Series getSeries();

  /**
   * measurement name
   */
  String getName();

  /**
   * tag map
   */
  Map<String, String> getTags();

  /**
   * 获取 tag 的值
   */
  String getTag(String name);

  /**
   * column position
   */
  int getPosition(String column);

  /**
   * 获取列
   */
  List<String> getColumns();

  /**
   * 获取列名
   */
  String getColumn(int index);

  /**
   * value list
   */
  List<List<Object>> getValues();

  /**
   * 获取某个下标的值集合
   *
   * @param position 位置
   * @return 返回对应位置上的值
   */
  List<Object> getValueList(int position);

  /**
   * 设置当前值的位置
   */
  ValueConverter setPosition(int position);

  /**
   * 获取当前值的位置
   */
  int getPosition();

  /**
   * 获取值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  <T> T getValue(List<Object> value, String column, T defaultValue);

  /**
   * 获取值
   *
   * @param column       字段名
   * @param defaultValue 默认值
   * @return 返回对应的值
   */
  Object getValue(String column, Object defaultValue);

  /**
   * 将时间戳转换为long类型
   */
  default long getTime() {
    String time = getString(TIME);
    return InfluxTimeUtil.fromInfluxDBTimeFormat(time);
  }

  /**
   * 将时间戳转换为Date类型
   */
  default Date getDate() {
    return new Date(getTime());
  }

  /**
   * 获取值
   *
   * @param column 字段
   * @return 返回获取的值
   */
  default Number getNumber(String column) {
    return getNumber(column, null);
  }

  /**
   * 获取值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  default Number getNumber(String column, Number defaultValue) {
    return (Number) getValue(column, defaultValue);
  }

  /**
   * 获取 int 类型的值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值或默认值
   */
  default int toInt(String column, int defaultValue) {
    Number number = getNumber(column);
    return number != null ? number.intValue() : defaultValue;
  }

  /**
   * 获取 long 类型的值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值或默认值
   */
  default long toLong(String column, long defaultValue) {
    Number number = getNumber(column);
    return number != null ? number.longValue() : defaultValue;
  }

  /**
   * 获取 short 类型的值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值或默认值
   */
  default short toShort(String column, short defaultValue) {
    Number number = getNumber(column);
    return number != null ? number.shortValue() : defaultValue;
  }

  /**
   * 获取 float 类型的值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值或默认值
   */
  default float toFloat(String column, float defaultValue) {
    Number number = getNumber(column);
    return number != null ? number.floatValue() : defaultValue;
  }

  /**
   * 获取 double 类型的值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值或默认值
   */
  default double toDouble(String column, double defaultValue) {
    Number number = getNumber(column);
    return number != null ? number.doubleValue() : defaultValue;
  }

  /**
   * 获取 boolean 类型的值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值或默认值
   */
  default boolean toBoolean(String column, boolean defaultValue) {
    String value = getString(column);
    return value != null ? Boolean.parseBoolean(value) : defaultValue;
  }

  /**
   * 获取值
   *
   * @param column 字段
   * @return 返回获取的值
   */
  default String getString(String column) {
    return getString(column, null);
  }

  /**
   * 获取值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  default String getString(String column, String defaultValue) {
    Object value = getValue(column, defaultValue);
    return value != null ? String.valueOf(value) : defaultValue;
  }

  /**
   * 获取Int值
   *
   * @param column       字段名
   * @param defaultValue 默认值
   * @return 返回获取后转换的值
   */
  default Integer getInteger(String column, Integer defaultValue) {
    Object value = getValue(column, null);
    return value != null ? Integer.valueOf(((Number) value).intValue()) : defaultValue;
  }

  /**
   * 获取Int值
   *
   * @param column 字段名
   * @return 返回获取后转换的值
   */
  default Integer getInteger(String column) {
    return getInteger(column, 0);
  }

  /**
   * 获取Short
   *
   * @param column 字段名
   * @return 返回获取后转换的值
   */
  default Short getShort(String column) {
    return getShort(column, null);
  }

  /**
   * 获取Short
   *
   * @param column       字段名
   * @param defaultValue 默认值
   * @return 返回获取后转换的值
   */
  default Short getShort(String column, Short defaultValue) {
    Object value = getValue(column, null);
    return value != null ? Short.valueOf(((Number) value).shortValue()) : defaultValue;
  }

  /**
   * 获取Short
   *
   * @param column 字段名
   * @return 返回获取后转换的值
   */
  default Long getLong(String column) {
    return getLong(column, null);
  }

  /**
   * 获取Short
   *
   * @param column       字段名
   * @param defaultValue 默认值
   * @return 返回获取后转换的值
   */
  default Long getLong(String column, Long defaultValue) {
    Object value = getValue(column, null);
    return value != null ? Long.valueOf(((Number) value).longValue()) : defaultValue;
  }

  /**
   * 获取Short
   *
   * @param column 字段名
   * @return 返回获取后转换的值
   */
  default Float getFloat(String column) {
    return getFloat(column, null);
  }

  /**
   * 获取Short
   *
   * @param column       字段名
   * @param defaultValue 默认值
   * @return 返回获取后转换的值
   */
  default Float getFloat(String column, Float defaultValue) {
    Object value = getValue(column, null);
    return value != null ? Float.valueOf(((Number) value).floatValue()) : defaultValue;
  }

  /**
   * 获取Short
   *
   * @param column 字段名
   * @return 返回获取后转换的值
   */
  default Double getDouble(String column) {
    return getDouble(column, null);
  }

  /**
   * 获取Short
   *
   * @param column       字段名
   * @param defaultValue 默认值
   * @return 返回获取后转换的值
   */
  default Double getDouble(String column, Double defaultValue) {
    Object value = getValue(column, null);
    return value != null ? Double.valueOf(((Number) value).doubleValue()) : defaultValue;
  }

  /**
   * 获取Short
   *
   * @param column 字段名
   * @return 返回获取后转换的值
   */
  default Boolean getBoolean(String column) {
    return getBoolean(column, null);
  }

  /**
   * 获取Short
   *
   * @param column       字段名
   * @param defaultValue 默认值
   * @return 返回获取后转换的值
   */
  default Boolean getBoolean(String column, Boolean defaultValue) {
    Object value = getValue(column, null);
    return value instanceof String ? Boolean.valueOf((String) value) : defaultValue;
  }

}
