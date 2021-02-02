package com.benefitj.spring.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface IMqttCallback extends MqttCallback, MqttTopicSubscribeAgainListener {

  @Override
  default void connectionLost(Throwable cause) {
    // ~
  }

  @Override
  void messageArrived(String topic, MqttMessage message) throws Exception;

  @Override
  default void deliveryComplete(IMqttDeliveryToken token) {
    // ~
  }

  /**
   * 客户端重连之后的重新订阅
   *
   * @param client 客户端
   */
  @Override
  default void onSubscribeAgain(SingleMqttClient client) {
    // ~
  }

}
