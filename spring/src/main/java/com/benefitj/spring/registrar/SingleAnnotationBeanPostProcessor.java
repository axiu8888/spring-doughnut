package com.benefitj.spring.registrar;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * 单个注解的后置处理器
 */
public abstract class SingleAnnotationBeanPostProcessor
    extends MethodAnnotationBeanPostProcessor implements TypeMetadataResolver {

  private Class<? extends Annotation> annotationType;

  public SingleAnnotationBeanPostProcessor() {
    this.setMetadataResolver(this);
  }

  public SingleAnnotationBeanPostProcessor(Class<? extends Annotation> annotationType) {
    this();
    this.annotationType = annotationType;
  }

  @Override
  public boolean support(Class<?> targetClass, Object bean, String beanName) {
    return true;
  }

  public Class<? extends Annotation> getAnnotationType() {
    return annotationType;
  }

  public void setAnnotationType(Class<? extends Annotation> annotationType) {
    this.annotationType = annotationType;
  }

  @SuppressWarnings("unchecked")
  @Override
  public AnnotationTypeMetadata resolve(Class<?> targetClass, Object bean, String beanName, BeanFactory beanFactory) {
    Collection<Method> methods = findAnnotationMethods(targetClass, getAnnotationType());
    if (methods.isEmpty()) {
      return null;
    }
    AnnotationTypeMetadata.MethodElement[] elements = methods.stream()
        .map(method -> new AnnotationTypeMetadata.MethodElement(
            method, method.getAnnotationsByType(getAnnotationType())))
        .toArray(AnnotationTypeMetadata.MethodElement[]::new);
    return new AnnotationTypeMetadata(targetClass, bean, beanName, elements);
  }

  @Override
  protected void doProcessAnnotations(ConcurrentMap<Class<?>, AnnotationTypeMetadata> typeMetadatas, ConfigurableListableBeanFactory beanFactory) {
    // 注册
    typeMetadatas.values()
        .stream()
        .filter(atm -> atm.getMethodElements().length > 0)
        .forEach(atm -> doProcessAnnotations0(atm, beanFactory));
  }

  /**
   * 注册
   *
   * @param metadata    元数据
   * @param beanFactory bean工程
   */
  protected abstract void doProcessAnnotations0(AnnotationTypeMetadata metadata, ConfigurableListableBeanFactory beanFactory);

}
