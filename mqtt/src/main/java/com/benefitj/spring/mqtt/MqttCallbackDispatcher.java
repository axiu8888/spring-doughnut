package com.benefitj.spring.mqtt;

import com.benefitj.mqtt.MqttMessageDispatcherImpl;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.Nullable;

/**
 * MQTT消息订阅与分发
 */
@Slf4j
public class MqttCallbackDispatcher extends MqttMessageDispatcherImpl<MqttMessage> implements IMqttCallback {

  @Override
  public void onConnected(SimpleMqttClient client, boolean reconnect) {
    // 重新订阅
    client.subscribe(getMqttTopicArray());
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    handleMessage(topic, message);
  }

  @Override
  public void onDisconnected(SimpleMqttClient client, @Nullable Throwable cause) {
    if (cause != null) {
      cause.printStackTrace();
    }
  }
}
