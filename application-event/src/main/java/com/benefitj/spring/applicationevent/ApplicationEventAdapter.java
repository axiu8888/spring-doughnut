package com.benefitj.spring.applicationevent;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class ApplicationEventAdapter implements ApplicationListener<ApplicationEvent> {

  private Object bean;
  private Method method;
  private Class<? extends ApplicationEvent> eventType;

  public ApplicationEventAdapter() {
  }

  public ApplicationEventAdapter(Object bean,
                                 Method method,
                                 Class<? extends ApplicationEvent> eventType) {
    this.bean = bean;
    this.method = method;
    this.eventType = eventType;
  }

  public boolean support(ApplicationEvent event) {
    return getEventType().isAssignableFrom(event.getClass());
  }

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    if (support(event)) {
      ReflectionUtils.invokeMethod(getMethod(), getBean(), event);
    }
  }

  public Object getBean() {
    return bean;
  }

  public void setBean(Object bean) {
    this.bean = bean;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public Class<? extends ApplicationEvent> getEventType() {
    return eventType;
  }

  public void setEventType(Class<? extends ApplicationEvent> eventType) {
    this.eventType = eventType;
  }
}
