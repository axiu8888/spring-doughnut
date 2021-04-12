package com.benefitj.spring.registrar;

/**
 * 注解元信息
 */
public class AnnotationMetadata {

  private static final MethodElement[] EMPTY_ELEMENT = new MethodElement[0];
  public static final AnnotationMetadata EMPTY = new AnnotationMetadata();

  private Class<?> targetClass;
  private Object bean;
  private String beanName;
  private MethodElement[] methodElements; // NOSONAR

  public AnnotationMetadata() {
  }

  public AnnotationMetadata(Class<?> targetClass, Object bean, String beanName) {
    this(targetClass, bean, beanName, EMPTY_ELEMENT);
  }

  public AnnotationMetadata(Class<?> targetClass,
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

}
