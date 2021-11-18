package com.benefitj.spring.mqtt;

import com.benefitj.mqtt.MqttMessageDispatcherImpl;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * MQTT消息分发
 */
public class SpringMqttMessageDispatcher
    extends MqttMessageDispatcherImpl<Message<?>> implements MessageHandler {

  @Override
  public void handleMessage(Message<?> message) throws MessagingException {
    handleMessage(MqttHeaders.getTopic(message), message);
  }

}
