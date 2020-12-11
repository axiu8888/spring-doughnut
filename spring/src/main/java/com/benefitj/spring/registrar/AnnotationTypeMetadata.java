package com.benefitj.spring.registrar;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationTypeMetadata {

  private static final MethodElement[] EMPTY_ELEMENT = new MethodElement[0];
  public static final AnnotationTypeMetadata EMPTY = new AnnotationTypeMetadata();

  private Class<?> targetClass;
  private Object bean;
  private String beanName;
  private MethodElement[] methodElements; // NOSONAR

  public AnnotationTypeMetadata() {
  }

  public AnnotationTypeMetadata(Class<?> targetClass, Object bean, String beanName) {
    this(targetClass, bean, beanName, EMPTY_ELEMENT);
  }

  public AnnotationTypeMetadata(Class<?> targetClass,
                                Object bean,
                                String beanName,
                                MethodElement[] methodElements) {
    this.targetClass = targetClass;
    this.bean = bean;
    this.beanName = beanName;
    this.methodElements = methodElements;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  public void setTargetClass(Class<?> targetClass) {
    this.targetClass = targetClass;
  }

  public Object getBean() {
    return bean;
  }

  public void setBean(Object bean) {
    this.bean = bean;
  }

  public String getBeanName() {
    return beanName;
  }

  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }

  public MethodElement[] getMethodElements() {
    return methodElements;
  }

  public void setMethodElements(MethodElement[] methodElements) {
    this.methodElements = methodElements;
  }

  /**
   * A method annotated with {@link Annotation}, together with the annotations.
   */
  public static class MethodElement {

    private Method method; // NOSONAR
    private Annotation[] annotations; // NOSONAR

    public MethodElement(Method method, Annotation[] annotations) { // NOSONAR
      this.method = method;
      this.annotations = annotations;
    }

    public Method getMethod() {
      return method;
    }

    public void setMethod(Method method) {
      this.method = method;
    }

    public Annotation[] getAnnotations() {
      return annotations;
    }

    public void setAnnotations(Annotation[] annotations) {
      this.annotations = annotations;
    }

  }

}
