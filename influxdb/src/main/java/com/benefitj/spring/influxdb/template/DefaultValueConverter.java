package com.benefitj.spring.influxdb.template;

import org.influxdb.dto.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认的值转换器
 */
public class DefaultValueConverter implements ValueConverter {

  /**
   * 序列
   */
  private QueryResult.Series series;
  /**
   * 存储映射Column位置的集合
   */
  private Map<String, int[]> positions = new HashMap<>();
  /**
   * 当前值的索引位置
   */
  private int position = -1;

  public DefaultValueConverter() {
  }

  public DefaultValueConverter(QueryResult.Series series) {
    this.setSeries(series);
  }

  protected DefaultValueConverter self() {
    return this;
  }

  @Override
  public DefaultValueConverter setSeries(QueryResult.Series series) {
    this.series = series;
    this.positions.clear();
    int size = getColumns().size();
    for (int i = 0; i < size; i++) {
      this.positions.put(getColumns().get(i), new int[]{i});
    }
    return self();
  }

  @Override
  public QueryResult.Series getSeries() {
    return series;
  }

  @Override
  public String getName() {
    return getSeries().getName();
  }

  /**
   * tag map
   */
  @Override
  public Map<String, String> getTags() {
    return getSeries().getTags();
  }

  /**
   * 获取 tag 的值
   */
  @Override
  public String getTag(String name) {
    Map<String, String> tags = getTags();
    return tags != null ? tags.get(name) : null;
  }

  /**
   * column position
   */
  @Override
  public int getPosition(String column) {
    int[] pos = positions.get(column);
    return pos != null ? pos[0] : -1;
  }

  /**
   * 获取列
   */
  @Override
  public List<String> getColumns() {
    return getSeries().getColumns();
  }

  /**
   * 获取列名
   */
  @Override
  public String getColumn(int index) {
    return getColumns().get(index);
  }

  /**
   * value list
   */
  @Override
  public List<List<Object>> getValues() {
    return getSeries().getValues();
  }

  /**
   * 获取
   *
   * @param position 位置
   * @return 返回对应位置上的值
   */
  @Override
  public List<Object> getValueList(int position) {
    List<List<Object>> values = getValues();
    return (position >= 0 && position < values.size()) ? values.get(position) : null;
  }

  /**
   * 设置当前值的位置
   */
  @Override
  public DefaultValueConverter setPosition(int position) {
    this.position = position;
    return self();
  }

  /**
   * 获取当前值的位置
   */
  @Override
  public int getPosition() {
    return position;
  }

  /**
   * 获取值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  @Override
  public <T> T getValue(List<Object> value, String column, T defaultValue) {
    int pos = getPosition(column);
    if (pos >= 0) {
      Object o = value.get(pos);
      return o != null ? (T) o : defaultValue;
    }
    return defaultValue;
  }

  /**
   * 获取值
   *
   * @param column       字段
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  @Override
  public Object getValue(String column, Object defaultValue) {
    if (getPosition() < 0) {
      throw new IllegalStateException("未设置当前值的位置");
    }
    return getValue(getValues().get(getPosition()), column, defaultValue);
  }


}
