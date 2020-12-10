package com.benefitj.spring.mqtt.config;

import com.benefitj.spring.eventbus.EnableAutoEventBusPoster;
import com.benefitj.spring.mqtt.MessagePublisherAdapter;
import com.benefitj.spring.mqtt.MqttOptionsProperty;
import com.benefitj.spring.mqtt.MqttPublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

/**
 * MQTT
 */
@EnableAutoEventBusPoster
@Configuration
public class MqttPublisherConfiguration extends AbstractMqttConfiguration {

  /**
   * 消息发布的客户端
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttPublisher mqttPublisher(MqttPahoClientFactory clientFactory,
                                     MqttOptionsProperty property) {
    return new MqttPublisher(property.getClientIdPrefix(), clientFactory);
  }

  /**
   * 蓝牙消息发送
   */
  @ConditionalOnMissingBean
  @Bean
  public MessagePublisherAdapter messagePublisherAdapter(MqttPublisher mqttPublisher) {
    return new MessagePublisherAdapter(mqttPublisher);
  }

}
