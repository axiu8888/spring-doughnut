package com.benefitj.spring.registrar;

import org.springframework.beans.factory.BeanFactory;

import javax.annotation.Nullable;

/**
 * bean注解解析器
 */
public interface TypeMetadataResolver {

  /**
   * 解析注解信息
   *
   * @param targetClass bean class
   * @param bean        bean实例
   * @param beanName    bean名称
   * @param beanFactory bean工厂
   * @return 返回结果的结果
   */
  @Nullable
  AnnotationTypeMetadata resolve(Class<?> targetClass, Object bean, String beanName, BeanFactory beanFactory);

}
