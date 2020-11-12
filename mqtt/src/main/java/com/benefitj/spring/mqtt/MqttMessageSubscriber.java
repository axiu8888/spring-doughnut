package com.benefitj.spring.mqtt;

import org.springframework.messaging.MessageHandler;

/**
 * MQTT消息订阅
 */
public interface MqttMessageSubscriber extends MessageHandler {

  /**
   * 默认的消息订阅者
   */
  MqttMessageSubscriber DISCARD_SUBSCRIBER = msg -> { /* discard */ };

}
