package com.benefitj.spring.eventbus;

import com.google.common.eventbus.Subscribe;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class EventBusAdapter {

  private Object bean;
  private Method method;
  private Class<?> eventType;

  public EventBusAdapter() {
  }

  public EventBusAdapter(Object bean,
                         Method method,
                         Class<Object> eventType) {
    this.bean = bean;
    this.method = method;
    this.eventType = eventType;
  }

  public boolean support(Object o) {
    return getEventType().isAssignableFrom(o.getClass());
  }

  /**
   * 接收事件
   *
   * @param event 事件
   */
  @Subscribe
  public void onEvent(Object event) {
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

  public Class<?> getEventType() {
    return eventType;
  }

  public void setEventType(Class<?> eventType) {
    this.eventType = eventType;
  }

}
