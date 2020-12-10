package com.benefitj.spring.mqtt.config;

import com.benefitj.spring.mqtt.MqttOptionsProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;

/**
 * MQTT消息订阅
 */
@Import({MqttListenerAnnotationBeanPostProcessor.class})
@Configuration
public class MqttSubscriberConfiguration extends AbstractMqttConfiguration {

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
  public DefaultMqttMessageListenerEndpointRegistry endpointRegistry(MqttOptionsProperty property,
                                                                     MqttPahoClientFactory clientFactory) {
    return new DefaultMqttMessageListenerEndpointRegistry(property, clientFactory);
  }

}
