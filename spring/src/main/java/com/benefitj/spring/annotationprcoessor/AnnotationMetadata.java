package com.benefitj.spring.annotationprcoessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 注解元信息
 */
public class AnnotationMetadata {
  /**
   * bean对象
   */
  private Object bean;
  /**
   * 方法
   */
  private Method method;
  /**
   * 注解
   */
  private Annotation annotation;

  public AnnotationMetadata() {
  }

  public AnnotationMetadata(Object bean, Method method, Annotation annotation) {
    this.bean = bean;
    this.method = method;
    this.annotation = annotation;
  }

  public Object getBean() {
    return bean;
  }

  public void setBean(Object bean) {
    this.bean = bean;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public Annotation getAnnotation() {
    return annotation;
  }

  public void setAnnotation(Annotation annotation) {
    this.annotation = annotation;
  }
}
