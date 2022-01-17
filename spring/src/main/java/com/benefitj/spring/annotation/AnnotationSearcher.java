package com.benefitj.spring.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 注解搜索器
 */
public class AnnotationSearcher implements BeanPostProcessor {

  /**
   * 注解解析器
   */
  private AnnotationResolver resolver;
  /**
   * 注解信息
   */
  private final List<AnnotationMetadata> metadatas = new ArrayList<>();

  public AnnotationSearcher() {
  }

  public AnnotationSearcher(AnnotationResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    getMetadatas().addAll(getResolver().resolve(bean, beanName));
    return bean;
  }

  public AnnotationResolver getResolver() {
    return resolver;
  }

  public void setResolver(AnnotationResolver resolver) {
    this.resolver = resolver;
  }

  /**
   * 解析的注解元信息
   */
  public List<AnnotationMetadata> getMetadatas() {
    return metadatas;
  }

  /**
   * 获取对应注解的元信息
   *
   * @param type 注解的类型
   * @return 返回元信息
   */
  public List<AnnotationMetadata> getMetadatas(Class<? extends Annotation> type) {
    return getMetadatas()
        .stream()
        .filter(metadata -> metadata.isAnnotationPresent(type))
        .collect(Collectors.toList());
  }

}
