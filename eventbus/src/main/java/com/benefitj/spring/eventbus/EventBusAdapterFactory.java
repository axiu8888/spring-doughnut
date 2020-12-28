package com.benefitj.spring.eventbus;

import java.lang.reflect.Method;

public interface EventBusAdapterFactory {

  /**
   * 创建 adapter
   *
   * @param bean      bean对象
   * @param method    方法
   * @param eventType 适配器类型
   * @return 返回事件的适配器
   */
  EventBusAdapter create(Object bean, Method method, Class<?> eventType);

}
