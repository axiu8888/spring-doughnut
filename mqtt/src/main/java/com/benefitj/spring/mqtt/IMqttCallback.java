package com.benefitj.spring.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.Nullable;

public interface IMqttCallback extends MqttCallback {

  /**
   * 客户端重连之后的重新订阅
   *
   * @param client    客户端
   * @param reconnect 是否为重连
   */
  default void onConnected(SimpleMqttClient client, boolean reconnect) {
    // ~
  }

  @Override
  void messageArrived(String topic, MqttMessage message) throws Exception;

  @Override
  default void deliveryComplete(IMqttDeliveryToken token) {
  }

  @Override
  default void connectionLost(Throwable cause) {
    if (cause != null) {
      cause.printStackTrace();
    }
  }

  /**
   * 连接断开
   * s
   *
   * @param client 客户端
   * @param cause  异常
   */
  default void onDisconnected(SimpleMqttClient client, @Nullable Throwable cause) {
    connectionLost(cause);
  }

}
