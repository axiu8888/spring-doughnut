package com.benefitj.spring.mqtt.configuration;

import com.benefitj.spring.mqtt.DefaultMqttMessageListenerRegistrar;
import com.benefitj.spring.mqtt.MqttMessageListenerAnnotationBeanPostProcessor;
import com.benefitj.spring.mqtt.MqttMessageListenerRegistrar;
import com.benefitj.spring.mqtt.MqttOptionsProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;

/**
 * MQTT消息订阅
 */
@Configuration
public class MqttSubscriberConfiguration {

  /**
   * 消息转换
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttMessageConverter mqttMessageConverter() {
    DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
    converter.setPayloadAsBytes(true);
    return converter;
  }

  /**
   * 监听器注册
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttMessageListenerRegistrar mqttMessageListenerRegistrar(MqttOptionsProperty property,
                                                                   MqttPahoClientFactory clientFactory) {
    return new DefaultMqttMessageListenerRegistrar(property, clientFactory);
  }

  /**
   * MQTT方法注解的后置处理器
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttMessageListenerAnnotationBeanPostProcessor mqttMessageListenerAnnotationBeanPostProcessor() {
    return new MqttMessageListenerAnnotationBeanPostProcessor();
  }

}
