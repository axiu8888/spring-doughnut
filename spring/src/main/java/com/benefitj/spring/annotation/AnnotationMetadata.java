package com.benefitj.spring.annotation;

import org.springframework.beans.factory.SmartInitializingSingleton;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
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
   * 目标类
   */
  private Class<?> targetClass;
  /**
   * 方法
   */
  private Method method;
  /**
   * 注解
   */
  private List<? extends Annotation> annotations = new LinkedList<>();
  /**
   * 调用过 {@link SmartInitializingSingleton#afterSingletonsInstantiated()} 方法
   */
  private volatile boolean instantiated = false;

  public AnnotationMetadata() {
  }

  public Object getBean() {
    return bean;
  }

  public void setBean(Object bean) {
    this.bean = bean;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  public void setTargetClass(Class<?> targetClass) {
    this.targetClass = targetClass;
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

  public void setAnnotations(List<? extends Annotation> annotations) {
    this.annotations = annotations;
  }

  /**
   * 添加注解
   */
  public void addAnnotations(Collection<? extends Annotation> c) {
    getAnnotations().addAll((Collection) c);
  }

  /**
   * 判断注解是否出现
   *
   * @param annotationType 注解类型
   * @return 返回是否出现
   */
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
    return getAnnotations().stream()
        .filter(annotation -> annotation.annotationType().isAssignableFrom(annotationType))
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

  /**
   * 获取类上的注解
   *
   * @param annotationType 注解类型
   * @param <A>            注解类型
   * @return 返回获取的注解对象
   */
  public <A extends Annotation> A findClassAnnotation(Class<A> annotationType) {
    return (A) getTargetClass().getAnnotation(annotationType);
  }

  /**
   * 判断类上是否出现某个注解
   *
   * @param annotationType 注解类型
   * @return 返回判断结果
   */
  public boolean isClassAnnotationPresent(Class<? extends Annotation> annotationType) {
    return getTargetClass().isAnnotationPresent(annotationType);
  }

  /**
   * 获取方法上的注解
   *
   * @param annotationType 注解类型
   * @param <A>            注解类型
   * @return 返回获取的注解对象
   */
  public <A extends Annotation> A findMethodAnnotation(Class<A> annotationType) {
    return getMethod().getAnnotation(annotationType);
  }

  /**
   * 判断方法上是否出现某个注解
   *
   * @param annotationType 注解类型
   * @return 返回判断结果
   */
  public boolean isMethodAnnotationPresent(Class<? extends Annotation> annotationType) {
    return getMethod().isAnnotationPresent(annotationType);
  }

  public boolean isInstantiated() {
    return instantiated;
  }

  public void setInstantiated(boolean instantiated) {
    this.instantiated = instantiated;
  }
}