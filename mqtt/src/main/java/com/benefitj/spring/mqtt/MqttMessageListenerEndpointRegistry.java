package com.benefitj.spring.mqtt;

import com.benefitj.spring.mqtt.annotaion.MqttMessageListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import java.lang.reflect.Method;

/**
 * 注册
 */
public interface MqttMessageListenerEndpointRegistry {

  /**
   * 注册
   *
   * @param bean                 bean实例
   * @param method               方法
   * @param endpoint             注解
   * @param handlerMethodFactory 处理工厂
   */
  void registerEndpoint(Object bean, Method method, MqttMessageListenerEndpoint endpoint, MessageHandlerMethodFactory handlerMethodFactory);

}
