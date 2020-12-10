package com.benefitj.spring.mqtt.config;

import com.benefitj.spring.mqtt.annotaion.MqttMessageListenerEndpoint;

import java.lang.reflect.Method;

/**
 * The metadata holder of the class with {@link MqttMessageListenerEndpoint}
 * and {@link MqttMessageListenerEndpoint} annotations.
 */
public class EndpointTypeMetadata {

  public static final EndpointTypeMetadata EMPTY = new EndpointTypeMetadata(new ListenerMethod[0]);

  private Object bean;
  private String beanName;
  private ListenerMethod[] listenerMethods; // NOSONAR

  public EndpointTypeMetadata(ListenerMethod[] methods) { // NOSONAR
    this(null, null, methods);
  }

  public EndpointTypeMetadata(Object bean, String beanName, ListenerMethod[] methods) {
    this.bean = bean;
    this.beanName = beanName;
    this.listenerMethods = methods;
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

  public ListenerMethod[] getListenerMethods() {
    return listenerMethods;
  }

  public void setListenerMethods(ListenerMethod[] listenerMethods) {
    this.listenerMethods = listenerMethods;
  }


  /**
   * A method annotated with {@link MqttMessageListenerEndpoint}, together with the annotations.
   */
  public static class ListenerMethod {

    private Method method; // NOSONAR
    private MqttMessageListenerEndpoint annotation; // NOSONAR

    public ListenerMethod(Method method, MqttMessageListenerEndpoint annotation) { // NOSONAR
      this.method = method;
      this.annotation = annotation;
    }

    public Method getMethod() {
      return method;
    }

    public void setMethod(Method method) {
      this.method = method;
    }

    public MqttMessageListenerEndpoint getAnnotation() {
      return annotation;
    }

    public void setAnnotation(MqttMessageListenerEndpoint annotation) {
      this.annotation = annotation;
    }
  }
}

