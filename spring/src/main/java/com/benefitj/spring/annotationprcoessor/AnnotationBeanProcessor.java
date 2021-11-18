package com.benefitj.spring.annotationprcoessor;

import com.benefitj.core.ReflectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 注解的处理器
 */
public class AnnotationBeanProcessor implements BeanPostProcessor, SmartInitializingSingleton {

  /**
   * 解析的所有方法的
   */
  private List<AnnotationMetadata> metadatas = new LinkedList<>();
  /**
   * 注解类型
   */
  private Class<? extends Annotation> annotationType;
  /**
   * 处理器
   */
  private MetadataHandler metadataHandler;

  public AnnotationBeanProcessor() {
  }

  public AnnotationBeanProcessor(Class<? extends Annotation> annotationType,
                                 MetadataHandler metadataHandler) {
    this.annotationType = annotationType;
    this.metadataHandler = metadataHandler;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    List<Method> methods = findMethods(bean.getClass(), getAnnotationType());
    getMetadatas().addAll(methods.stream()
        .map(method -> resolveMetadata(bean, method))
        .filter(Objects::nonNull)
        .collect(Collectors.toList()));
    return bean;
  }

  @Nullable
  public AnnotationMetadata resolveMetadata(Object bean, Method method) {
    return new AnnotationMetadata(bean, method, method.getAnnotation(getAnnotationType()));
  }

  @Override
  public void afterSingletonsInstantiated() {
    getMetadataHandler().handle(getMetadatas());
  }

  public List<AnnotationMetadata> getMetadatas() {
    return metadatas;
  }

  public Class<? extends Annotation> getAnnotationType() {
    return annotationType;
  }

  public void setAnnotationType(Class<? extends Annotation> annotationType) {
    this.annotationType = annotationType;
  }

  public MetadataHandler getMetadataHandler() {
    return metadataHandler;
  }

  public void setMetadataHandler(MetadataHandler metadataHandler) {
    this.metadataHandler = metadataHandler;
  }

  /**
   * 查找匹配的方法
   *
   * @param clazz          类
   * @param annotationType 注解类型
   * @return 返回匹配的方法
   */
  public static List<Method> findMethods(Class<?> clazz, Class<? extends Annotation> annotationType) {
    return findMethods(clazz, m -> m.isAnnotationPresent(annotationType));
  }

  /**
   * 查找匹配的方法
   *
   * @param clazz  类
   * @param filter 匹配器
   * @return 返回匹配的方法
   */
  public static List<Method> findMethods(Class<?> clazz, Predicate<Method> filter) {
    return ReflectUtils.getMethods(clazz, filter);
  }

}