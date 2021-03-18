package com.benefitj.spring.registrar;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * 可注册的方法注解的后置处理器
 */
public class RegistrarMethodAnnotationBeanPostProcessor
    extends MethodAnnotationBeanPostProcessor implements TypeMetadataResolver {

  /**
   * 注册器
   */
  private AnnotationListenerRegistrar registrar;
  /**
   * 注解类型
   */
  private Class<? extends Annotation> annotationType;

  public RegistrarMethodAnnotationBeanPostProcessor(AnnotationListenerRegistrar registrar,
                                                    Class<? extends Annotation> annotationType) {
    this.setMetadataResolver(this);
    this.setRegistrar(registrar);
    this.setAnnotationType(annotationType);
  }

  @Override
  public boolean support(Class<?> targetClass, Object bean, String beanName) {
    return true;
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
        .forEach(atm -> getRegistrar().register(atm, beanFactory));
  }

  public AnnotationListenerRegistrar getRegistrar() {
    return registrar;
  }

  public void setRegistrar(AnnotationListenerRegistrar registrar) {
    this.registrar = registrar;
  }

  public Class<? extends Annotation> getAnnotationType() {
    return annotationType;
  }

  public void setAnnotationType(Class<? extends Annotation> annotationType) {
    this.annotationType = annotationType;
  }
}
