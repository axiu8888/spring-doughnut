package com.benefitj.spring.annotation;

import com.benefitj.core.ReflectUtils;
import com.benefitj.core.concurrent.ConcurrentHashSet;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 注解解析器
 */
public class AnnotationResolverImpl implements AnnotationResolver {

  /**
   * 注解
   */
  private final Set<Class<? extends Annotation>> annotationTypes = new ConcurrentHashSet<>();
  /**
   * 是否全部匹配，默认匹配任何一个
   */
  private boolean allMatches = false;

  public AnnotationResolverImpl() {
  }

  public AnnotationResolverImpl(Class<? extends Annotation> annotationType) {
    this(Collections.singletonList(annotationType), false);
  }

  public AnnotationResolverImpl(Collection<Class<? extends Annotation>> annotationTypes,
                                boolean allMatches) {
    this.allMatches = allMatches;
    this.annotationTypes.addAll(annotationTypes);
  }

  @Override
  public Collection<? extends AnnotationMetadata> resolve(Object bean, String beanName) {
    Class<?> targetClass = getTargetClass(bean);
    return ReflectUtils.getMethods(targetClass, m -> support(bean, m))
        .stream()
        .map(method -> resolveMetadata(bean, method))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public Set<Class<? extends Annotation>> getAnnotationTypes() {
    return annotationTypes;
  }

  /**
   * 是否全部匹配
   */
  public boolean isAllMatches() {
    return allMatches;
  }

  /**
   * 设置是否全部匹配
   *
   * @param allMatches 全部匹配的规则
   */
  public void setAllMatches(boolean allMatches) {
    this.allMatches = allMatches;
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

  public Class<?> getTargetClass(Object bean) {
    return AopUtils.getTargetClass(bean);
  }

  /**
   * 判断方法是否支持
   *
   * @param bean   Bean对象
   * @param method 方法
   * @return 返回判断结果
   */
  public boolean support(Object bean, Method method) {
    return ReflectUtils.isAnnotationPresent(method, getAnnotationTypes(), isAllMatches());
  }

  /**
   * 解析元信息
   *
   * @param bean   bean对象
   * @param method 方法
   * @return 返回注解元信息
   */
  public AnnotationMetadata resolveMetadata(Object bean, Method method) {
    List<? extends Annotation> annotations = resolveAnnotations(method);
    AnnotationMetadata metadata = new AnnotationMetadata();
    metadata.setBean(bean);
    metadata.setTargetClass(AopUtils.getTargetClass(bean));
    metadata.setMethod(method);
    metadata.addAnnotations(annotations);
    return metadata;
  }

  /**
   * 获取注解对象
   *
   * @param method 方法
   * @return 返回注解对象
   */
  public List<? extends Annotation> resolveAnnotations(Method method) {
    return this.getAnnotationTypes()
        .stream()
        .filter(method::isAnnotationPresent)
        .map(annotationType -> AnnotationUtils.getAnnotation(method, annotationType))
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return getClass().getName() + "("+ getAnnotationTypes() +")";
  }
}
