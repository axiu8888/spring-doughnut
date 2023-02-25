package com.benefitj.influxdb.convert;


import com.benefitj.influxdb.dto.QueryResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Converter 工厂
 */
public interface ConverterFactory<U> {

  /**
   * 获取对象类型的转换器
   *
   * @param type item类型
   * @return 返回类型对应的转换器
   */
  <T> Converter<T, U> getConverter(Class<T> type);

  /**
   * 转换对象
   *
   * @param record 对象
   * @return 返回转换后的对象
   */
  <T> U convert(T record);

  /**
   * 转换对象
   *
   * @param records 对象数组
   * @return 返回转换后的对象集合
   */
  default <T> List<U> convert(T[] records) {
    return convert(Arrays.asList(records));
  }

  /**
   * 转换对象
   *
   * @param records 对象集合
   * @return 返回转换后的对象集合
   */
  <T> List<U> convert(Collection<T> records);

  /**
   * 转换成bean对象
   *
   * @param result 查询的结果集
   * @param type   bean类型
   * @param <T>    泛型类型
   * @return 返回解析的对象
   */
  <T> List<T> mapperTo(QueryResult result, Class<T> type);

}
