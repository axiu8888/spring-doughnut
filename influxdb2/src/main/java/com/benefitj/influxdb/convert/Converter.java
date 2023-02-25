package com.benefitj.influxdb.convert;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 转换器
 *
 * @param <T> 原对象
 * @param <U> 目标对象
 */
public interface Converter<T, U> {

  /**
   * 转换为行协议
   *
   * @param record 需要转换的 record
   * @return 转换后的数据
   */
  U convert(T record);

  /**
   * 转换为行协议
   *
   * @param records 需要转换的数组
   * @return 返回转换后的集合
   */
  default List<U> convert(T[] records) {
    return convert(Arrays.asList(records));
  }

  /**
   * 转换为行协议
   *
   * @param records 需要转换的集合
   * @return 返回转换后的集合
   */
  default List<U> convert(Collection<T> records) {
    return records.stream()
        .filter(Objects::nonNull)
        .flatMap((Function<T, Stream<U>>) t -> Stream.of(convert(t)))
        .collect(Collectors.toList());
  }

  /**
   * 类型
   */
  Class<T> getType();

  /**
   * MEASUREMENT
   */
  String getMeasurement();

  /**
   * 获取 TAG
   */
  Map<String, ColumnField> getTags();

  /**
   * 获取 column
   */
  Map<String, ColumnField> getColumns();

  /**
   * 时间戳
   */
  ColumnField getTimestamp();

  /**
   * 时间戳单位
   */
  TimeUnit getTimestampUnit();

  /**
   * 设置时间戳单位
   */
  void setTimestampUnit(TimeUnit unit);

}
