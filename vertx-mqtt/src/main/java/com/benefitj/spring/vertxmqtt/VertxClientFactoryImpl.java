package com.benefitj.spring.vertxmqtt;

import com.benefitj.core.IdUtils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.mqtt.vertx.client.AutoConnectTimer;
import com.benefitj.mqtt.vertx.client.VertxMqttClient;

/**
 * 客户端工厂实现
 */
public class VertxClientFactoryImpl implements VertxClientFactory {

  private Class<? extends VertxMqttClient> clientType = VertxMqttClient.class;

  public VertxClientFactoryImpl() {
  }

  public VertxClientFactoryImpl(Class<? extends VertxMqttClient> clientType) {
    this.clientType = clientType;
  }

  public Class<? extends VertxMqttClient> getClientType() {
    return clientType;
  }

  public void setClientType(Class<? extends VertxMqttClient> clientType) {
    this.clientType = clientType;
  }

  /**
   * 创建客户端
   *
   * @param property 配置
   * @return 返回新创建的客户端
   */
  @Override
  public VertxMqttClient create(MqttClientProperty property) {
    return setup(ReflectUtils.newInstance(getClientType()), property);
  }

  /**
   * 设置客户端参数
   *
   * @param client   客户端
   * @param property 参数配置
   * @return 返回设置好的客户端
   */
  public static VertxMqttClient setup(VertxMqttClient client, MqttClientProperty property) {
    MqttClientProperty.AutoReconnect reconnect = property.getAutoReconnect();
    client.setRemoteAddress(property.getHost(), property.getPort())
        .setAutoConnectTimer(new AutoConnectTimer()
            .setPeriod(reconnect.getPeriod())
            .setUnit(reconnect.getTimeUnit())
            .setAutoConnect(reconnect.isAuto()));
    client.getOptions()
        .setClientId(IdUtils.nextLetterId(property.getClientIdPrefix(), null, 10))
        .setUsername(property.getUsername())
        .setPassword(property.getPassword())
        .setCleanSession(property.isCleanSession())
        .setWillTopic(property.getWillTopic())
        .setWillMessage(property.getWillMessage())
        .setWillFlag(property.isWillFlag())
        .setWillQoS(property.getWillQos())
        .setWillRetain(property.isWillRetain())
        .setAutoKeepAlive(property.isAutoKeepAlive())
        .setKeepAliveInterval(property.getKeepAliveInterval())
        .setMaxInflightQueue(property.getMaxInflightQueue())
        .setMaxMessageSize(property.getMaxMessageSize())
        .setAckTimeout(property.getAckTimeout());
    return client;
  }

}