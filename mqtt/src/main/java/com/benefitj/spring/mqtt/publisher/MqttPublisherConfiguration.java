package com.benefitj.spring.mqtt.publisher;

import com.benefitj.spring.mqtt.CommonsMqttConfiguration;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT
 */
@Configuration
public class MqttPublisherConfiguration extends CommonsMqttConfiguration {

  /**
   * 客户端ID前缀
   */
  @Value("#{@environment['spring.mqtt.publisher.client-id-prefix'] ?: 'mqtt-publisher-'}")
  private String clientIdPrefix;

  /**
   * 发布的客户端数量
   */
  @Value("#{@environment['spring.mqtt.publisher.client-count'] ?: 1}")
  private int clientCount;

  /**
   * 消息发布的客户端
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttPublisher mqttPublisher(MqttConnectOptions options) {
    return new MqttPublisher(options, clientIdPrefix, clientCount);
  }

}
