package com.benefitj.spring;

import org.springframework.beans.BeanUtils;

/**
 * Bean 工具类
 */
public class BeanHelper {
  /**
   * 创建对象
   *
   * @param klass 类
   * @param <T>   返回的对象类型
   * @return 返回创建的对象
   */
  public static <T> T newInstance(Class<T> klass) {
    try {
      return (T) klass.newInstance();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   */
  public static <R> R copy(R r) {
    return copy(r, (Class<R>) r.getClass());
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   */
  public static <R, U> U copy(R r, Class<U> targetClass) {
    return copy(r, (U) newInstance(targetClass));
  }

  /**
   * 拷贝对象，返回新的目标类型的对象
   */
  public static <R, U> U copy(R r, U u) {
    BeanUtils.copyProperties(r, u);
    return u;
  }

}