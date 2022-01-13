package com.benefitj.spring.annotation;

import com.benefitj.core.ReflectUtils;
import com.benefitj.core.concurrent.ConcurrentHashSet;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 注解搜索器
 */
public class AnnotationSearcher implements BeanPostProcessor {
  /**
   * 查找的注解类型
   */
  private final Set<Class<? extends Annotation>> annotationTypes = new ConcurrentHashSet<>();
  /**
   * 注解信息
   */
  private final List<AnnotationMetadata> metadatas = new ArrayList<>();

  public AnnotationSearcher() {
  }

  public AnnotationSearcher(List<Class<Annotation>> annotationTypes) {
    this.annotationTypes.addAll(annotationTypes);
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    List<AnnotationMetadata> list = ReflectUtils.getMethods(targetClass, m -> support(bean, m))
        .stream()
        .map(method -> resolveMetadata(bean, method))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    getMetadatas().addAll(list);
    return bean;
  }

  /**
   * 判断方法是否支持
   *
   * @param bean   Bean对象
   * @param method 方法
   * @return 返回判断结果
   */
  protected boolean support(Object bean, Method method) {
    return getAnnotationTypes().stream().anyMatch(method::isAnnotationPresent);
  }

  /**
   * 解析元信息
   *
   * @param bean   bean对象
   * @param method 方法
   * @return 返回注解元信息
   */
  protected AnnotationMetadata resolveMetadata(Object bean, Method method) {
    return new AnnotationMetadata(bean, method, resolveAnnotations(method));
  }

  /**
   * 获取注解对象
   *
   * @param method 方法
   * @return 返回注解对象
   */
  protected List<? extends Annotation> resolveAnnotations(Method method) {
    return this.getAnnotationTypes().stream()
        .filter(method::isAnnotationPresent)
        .map(method::getAnnotation)
        .collect(Collectors.toList());
  }

  /**
   * 注册注解类型
   *
   * @param annotation 注解类型
   */
  public void register(Class<? extends Annotation> annotation) {
    register(Collections.singletonList(annotation));
  }

  /**
   * 注册注解类型
   *
   * @param annotations 注解类型
   */
  public void register(List<Class<? extends Annotation>> annotations) {
    this.annotationTypes.addAll(annotations);
  }

  /**
   * 取消注解类型注册
   *
   * @param annotations 注解类型
   */
  public void unregister(List<Class<? extends Annotation>> annotations) {
    this.annotationTypes.removeAll(annotations);
  }

  /**
   * 注解类型
   */
  public Set<Class<? extends Annotation>> getAnnotationTypes() {
    return annotationTypes;
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
