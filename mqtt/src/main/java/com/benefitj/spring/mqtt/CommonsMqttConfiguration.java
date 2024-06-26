package com.benefitj.spring.mqtt;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

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
  public MqttProperty mqttProperty() {
    return new MqttProperty();
  }

  /**
   * MQTT连接器选项
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttConnectOptions mqttConnectOptions(MqttProperty property) {
    MqttConnectOptions options = new MqttConnectOptions();
    // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
    // 这里设置为true表示每次连接到服务器都以新的身份连接
    options.setCleanSession(property.isCleanSession());
    // 设置连接的用户名
    options.setUserName(property.getUsername());
    // 设置连接的密码
    options.setPassword((StringUtils.isNotBlank(property.getPassword()) ? property.getPassword() : "").toCharArray());
    options.setServerURIs(StringUtils.split(property.getServerURIs(), ","));
    // 设置超时时间 单位为秒
    options.setConnectionTimeout(property.getConnectionTimeout());
    // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
    options.setKeepAliveInterval(property.getKeepalive());
    // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
    MqttProperty.Will will = property.getWill();
    if (StringUtils.isNotBlank(will.getPayload())) {
      options.setWill(will.getTopic(), will.getPayload().getBytes(StandardCharsets.UTF_8), will.getQos(), will.isRetained());
    }
    // 自动重连
    options.setAutomaticReconnect(property.isAutomaticReconnect());
    // 最大发送数量
    options.setMaxInflight(property.getMaxInflight());
    return options;
  }


}
