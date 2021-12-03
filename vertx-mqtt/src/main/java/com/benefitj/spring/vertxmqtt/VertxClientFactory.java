package com.benefitj.spring.vertxmqtt;

import com.benefitj.mqtt.vertx.client.VertxMqttClient;

/**
 * MQTT客户端工厂
 */
public interface VertxClientFactory {

  /**
   * 创建客户端
   *
   * @param property 配置
   * @return 返回新创建的客户端
   */
  VertxMqttClient create(MqttClientProperty property);

  /**
   * 创建客户端工厂
   *
   * @return 返回客户端工厂
   */
  static VertxClientFactory newFactory() {
    return newFactory(VertxMqttClient.class);
  }

  /**
   * 创建客户端工厂
   *
   * @param clientType 客户端对象
   * @return 返回客户端工厂
   */
  static VertxClientFactory newFactory(Class<? extends VertxMqttClient> clientType) {
    return new VertxClientFactoryImpl(clientType);
  }

}
