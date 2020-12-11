package com.benefitj.spring.mqtt;

import com.benefitj.spring.registrar.AnnotationTypeMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 消息订阅器
 *
 * @author DINGXIUAN
 */
public interface MqttMessageListenerRegistrar {

  /**
   * 注册
   *
   * @param metadata    注解类的信息
   * @param beanFactory bean工厂
   */
  void register(AnnotationTypeMetadata metadata, ConfigurableListableBeanFactory beanFactory);

}
