package com.benefitj.spring.annotation;

import java.util.Collection;

/**
 * 解析注解
 */
public interface AnnotationResolver {

  /**
   * 解析
   *
   * @param bean     Bean实例
   * @param beanName Bean名称
   * @return 返回解析的注解信息
   */
  Collection<? extends AnnotationMetadata> resolve(Object bean, String beanName);

}
