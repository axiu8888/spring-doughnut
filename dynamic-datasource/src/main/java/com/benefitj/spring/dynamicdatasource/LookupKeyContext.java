package com.benefitj.spring.dynamicdatasource;

/**
 * 数据源的key
 */
public interface LookupKeyContext<T> {

  /**
   * 获取数据源
   */
  T get();

  /**
   * 设置数据源
   *
   * @param key
   */
  void set(T key);

  /**
   * 移除数据源
   */
  void remove();

  /**
   * 获取并设置新的key
   *
   * @param key 新的Key
   * @return 返回旧的key
   */
  T getAndSet(T key);

}
