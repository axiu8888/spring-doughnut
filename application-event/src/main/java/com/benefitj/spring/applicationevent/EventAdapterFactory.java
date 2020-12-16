package com.benefitj.spring.applicationevent;

import org.springframework.context.ApplicationEvent;

import java.lang.reflect.Method;

public interface EventAdapterFactory {

  /**
   * 创建 adapter
   *
   * @param bean      bean对象
   * @param method    方法
   * @param eventType 适配器类型
   * @return 返回事件的适配器
   */
  ApplicationEventAdapter create(Object bean,
                                 Method method,
                                 Class<? extends ApplicationEvent> eventType);

}
