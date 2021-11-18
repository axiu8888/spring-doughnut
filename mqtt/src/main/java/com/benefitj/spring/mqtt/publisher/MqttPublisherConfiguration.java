package com.benefitj.spring.mqtt.publisher;

import com.benefitj.spring.mqtt.CommonsMqttConfiguration;
import com.benefitj.spring.mqtt.MqttOptionsProperty;
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
    String clientIdPrefix = property.getClientIdPrefix();
    Integer clientCount = property.getClientCount();
    return new MqttPublisher(clientFactory, clientIdPrefix, clientCount);
  }

}
