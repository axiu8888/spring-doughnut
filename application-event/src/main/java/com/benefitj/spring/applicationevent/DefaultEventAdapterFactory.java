package com.benefitj.spring.applicationevent;

import org.springframework.context.ApplicationEvent;

import java.lang.reflect.Method;

/**
 * 默认的适配器工厂
 */
public class DefaultEventAdapterFactory implements EventAdapterFactory {

  @Override
  public ApplicationEventAdapter create(Object bean,
                                        Method method,
                                        Class<? extends ApplicationEvent> eventType) {
    ApplicationEventAdapter adapter = new ApplicationEventAdapter();
    adapter.setBean(bean);
    adapter.setMethod(method);
    adapter.setEventType(eventType);
    return adapter;
  }

}
