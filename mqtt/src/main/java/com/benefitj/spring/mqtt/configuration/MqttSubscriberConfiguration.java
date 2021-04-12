package com.benefitj.spring.mqtt.configuration;

import com.benefitj.spring.mqtt.MqttMessageListener;
import com.benefitj.spring.mqtt.MqttMessageMetadataRegistrar;
import com.benefitj.spring.mqtt.MqttOptionsProperty;
import com.benefitj.spring.registrar.RegistrarMethodAnnotationBeanPostProcessor;
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
  public MqttMessageMetadataRegistrar mqttMessageListenerRegistrar(MqttOptionsProperty property,
                                                                   MqttPahoClientFactory clientFactory) {
    return new MqttMessageMetadataRegistrar(property, clientFactory);
  }

  /**
   * MQTT方法注解的后置处理器
   */
  @ConditionalOnMissingBean(name = "mqttMessageListenerProcessor")
  @Bean("mqttMessageListenerProcessor")
  public RegistrarMethodAnnotationBeanPostProcessor mqttMessageListenerProcessor(MqttMessageMetadataRegistrar registrar) {
    return new RegistrarMethodAnnotationBeanPostProcessor(registrar, MqttMessageListener.class);
  }

}
