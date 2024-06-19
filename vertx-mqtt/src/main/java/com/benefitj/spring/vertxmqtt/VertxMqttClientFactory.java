package com.benefitj.spring.vertxmqtt;

import com.benefitj.core.IdUtils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.vertx.AutoConnectTimer;
import com.benefitj.vertx.mqtt.client.VertxMqttClient;
import org.apache.commons.lang3.StringUtils;

/**
 * MQTT客户端工厂
 */
public interface VertxMqttClientFactory<T extends VertxMqttClient> {

  /**
   * 创建客户端
   *
   * @param options 配置
   * @return 返回新创建的客户端
   */
  T create(MqttClientOptions options);

  /**
   * 创建客户端工厂
   *
   * @return 返回客户端工厂
   */
  static VertxMqttClientFactory<VertxMqttClient> newFactory() {
    return newFactory(VertxMqttClient.class);
  }

  /**
   * 创建客户端工厂
   *
   * @param clientType 客户端对象
   * @return 返回客户端工厂
   */
  static <T extends VertxMqttClient> VertxMqttClientFactory<T> newFactory(Class<T> clientType) {
    return new Impl<>(clientType);
  }


  class Impl<T extends VertxMqttClient> implements VertxMqttClientFactory<T> {

    private Class<T> clientType;

    public Impl(Class<T> clientType) {
      this.clientType = clientType;
    }

    public Class<T> getClientType() {
      return clientType;
    }

    public void setClientType(Class<T> clientType) {
      this.clientType = clientType;
    }

    /**
     * 创建客户端
     *
     * @param options 配置
     * @return 返回新创建的客户端
     */
    @Override
    public T create(MqttClientOptions options) {
      return setup(ReflectUtils.newInstance(getClientType()), options);
    }

  }


  /**
   * 设置客户端参数
   *
   * @param client  客户端
   * @param options 参数配置
   * @return 返回设置好的客户端
   */
  static <T extends VertxMqttClient> T setup(T client,
                                             MqttClientOptions options) {
    MqttClientOptions.AutoReconnect reconnect = options.getAutoReconnect();
    client.setRemoteAddress(options.getHost(), options.getPort())
        .setAutoConnectTimer(new AutoConnectTimer()
            .setPeriod(reconnect.getPeriod())
            .setUnit(reconnect.getTimeUnit())
            .setAutoConnect(reconnect.isAuto()));
    client.getOptions()
        .setClientId(IdUtils.nextLetterId(options.getClientIdPrefix(), null, 10))
        .setUsername(options.getUsername())
        .setPassword(options.getPassword())
        .setCleanSession(options.isCleanSession())
        .setWillTopic(options.getWillTopic())
        .setWillMessage(StringUtils.getIfBlank(options.getWillMessage(), () -> ""))
        .setWillFlag(options.isWillFlag())
        .setWillQoS(options.getWillQos())
        .setWillRetain(options.isWillRetain())
        .setAutoKeepAlive(options.isAutoKeepAlive())
        .setKeepAliveInterval(options.getKeepAliveInterval())
        .setMaxInflightQueue(options.getMaxInflightQueue())
        .setMaxMessageSize(options.getMaxMessageSize())
        .setAckTimeout(options.getAckTimeout());
    return client;
  }


}
