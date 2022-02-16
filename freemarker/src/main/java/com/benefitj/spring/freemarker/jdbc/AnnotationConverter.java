package com.benefitj.spring.freemarker.jdbc;

import java.lang.annotation.Annotation;

/**
 * 将注解转换为字符串
 */
public interface AnnotationConverter<T extends Annotation> {

  /**
   * 转换
   *
   * @param annotation 注解
   * @return 返回注解值
   */
  String convert(T annotation);

}
