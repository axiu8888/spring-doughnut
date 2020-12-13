package com.benefitj.spring.mqtt.config;

import com.benefitj.spring.mqtt.MqttOptionsProperty;
import com.benefitj.spring.mqtt.MqttPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

/**
 * MQTT
 */
@Configuration
public class MqttPublisherConfiguration extends CommonsMqttConfiguration {

  /**
   * 消息发布的客户端
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttPublisher mqttPublisher(MqttPahoClientFactory clientFactory,
                                     MqttOptionsProperty property) {
    return new MqttPublisher(clientFactory, property.getClientIdPrefix());
  }

}
