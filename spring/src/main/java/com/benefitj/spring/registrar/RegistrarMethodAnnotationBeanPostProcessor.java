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
public class RegistrarMethodAnnotationBeanPostProcessor extends MethodAnnotationBeanPostProcessor
    implements MetadataResolver {

  /**
   * 注册器
   */
  private AnnotationMetadataRegistrar registrar;
  /**
   * 注解类型
   */
  private Class<? extends Annotation> annotationType;

  public RegistrarMethodAnnotationBeanPostProcessor(AnnotationMetadataRegistrar registrar,
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
  public AnnotationMetadata resolve(Class<?> targetClass, Object bean, String beanName, BeanFactory beanFactory) {
    Collection<Method> methods = findAnnotationMethods(targetClass, getAnnotationType());
    if (!methods.isEmpty()) {
      MethodElement[] elements = methods.stream()
          .map(method -> new MethodElement(method, method.getAnnotationsByType(getAnnotationType())))
          .toArray(MethodElement[]::new);
      return new AnnotationMetadata(targetClass, bean, beanName, elements);
    }
    return null;
  }

  @Override
  protected void doProcessAnnotations(ConcurrentMap<Class<?>, AnnotationMetadata> typeMetadatas, ConfigurableListableBeanFactory beanFactory) {
    // 注册
    typeMetadatas.values()
        .stream()
        .filter(atm -> atm.getMethodElements().length > 0)
        .forEach(atm -> getRegistrar().register(atm, beanFactory));
  }

  public AnnotationMetadataRegistrar getRegistrar() {
    return registrar;
  }

  public void setRegistrar(AnnotationMetadataRegistrar registrar) {
    this.registrar = registrar;
  }

  public Class<? extends Annotation> getAnnotationType() {
    return annotationType;
  }

  public void setAnnotationType(Class<? extends Annotation> annotationType) {
    this.annotationType = annotationType;
  }
}
