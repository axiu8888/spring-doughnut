package com.benefitj.spring.mqtt.config;

import com.benefitj.spring.mqtt.MqttOptionsProperty;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

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
  public MqttOptionsProperty mqttOptionsProperty() {
    return new MqttOptionsProperty();
  }

  /**
   * MQTT连接器选项
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttConnectOptions mqttConnectOptions(MqttOptionsProperty property) {
    MqttConnectOptions options = new MqttConnectOptions();
    // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
    // 这里设置为true表示每次连接到服务器都以新的身份连接
    options.setCleanSession(property.getCleanSession());
    // 设置连接的用户名
    options.setUserName(property.getUsername());
    // 设置连接的密码
    options.setPassword(property.getPassword().toCharArray());
    options.setServerURIs(StringUtils.split(property.getServerURIs(), ","));
    // 设置超时时间 单位为秒
    options.setConnectionTimeout(property.getConnectionTimeout());
    // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
    options.setKeepAliveInterval(property.getKeepalive());
    // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
    //options.setWill("willTopic", WILL_DATA, 2, false);
    // 自动重连
    options.setAutomaticReconnect(property.getAutomaticReconnect());
    return options;
  }

  /**
   * MQTT客户端
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttPahoClientFactory mqttClientFactory(MqttConnectOptions options) {
    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    factory.setConnectionOptions(options);
    return factory;
  }

}
