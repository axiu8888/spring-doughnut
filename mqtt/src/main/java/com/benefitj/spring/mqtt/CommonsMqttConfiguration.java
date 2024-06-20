package com.benefitj.spring.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT服务配置
 */
@EnableConfigurationProperties
@Configuration
public class CommonsMqttConfiguration {

  /**
   * MQTT配置
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttOptions mqttOptions() {
    return new MqttOptions();
  }

  /**
   * MQTT连接器选项
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttConnectOptions mqttConnectOptions(MqttOptions options) {
    return options.toMqttConnectOptions();
  }

}
