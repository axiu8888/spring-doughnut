package com.benefitj.spring.eventbus;

import java.lang.reflect.Method;

/**
 * 默认的adapter工厂实现
 */
public class DefaultEventBusAdapterFactory implements EventBusAdapterFactory {

  @Override
  public EventBusAdapter create(Object bean, Method method, Class<?> eventType) {
    EventBusAdapter adapter = new EventBusAdapter();
    adapter.setBean(bean);
    adapter.setMethod(method);
    adapter.setEventType(eventType);
    return adapter;
  }

}
