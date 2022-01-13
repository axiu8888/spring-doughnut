package com.benefitj.spring.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
  private final List<? extends Annotation> annotations;

  public AnnotationMetadata() {
    this.annotations = new ArrayList<>();
  }

  public AnnotationMetadata(Object bean, Method method, List<? extends Annotation> annotations) {
    this.bean = bean;
    this.method = method;
    this.annotations = annotations;
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

  public List<? extends Annotation> getAnnotations() {
    return annotations;
  }

  public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
    return getMethod().isAnnotationPresent(annotationType);
  }

  /**
   * 获取注解对象
   *
   * @param annotationType 注解类型
   * @param <T>            注解类型
   * @return 返回获取的注解对象
   */
  public <T extends Annotation> List<T> getAnnotations(Class<T> annotationType) {
    return annotations.stream()
        .filter(annotation -> annotation.getClass().isAssignableFrom(annotationType))
        .map(annotation -> (T) annotation)
        .collect(Collectors.toList());
  }

  /**
   * 获取注解对象
   *
   * @param annotationType 注解类型
   * @param <T>            注解类型
   * @return 返回获取的注解对象
   */
  public <T extends Annotation> T getFirstAnnotation(Class<T> annotationType) {
    return getAnnotations(annotationType)
        .stream()
        .findFirst()
        .orElse(null);
  }

}