package com.benefitj.mqttsubscriber;

import com.benefitj.spring.applicationevent.EnableAutoApplicationListener;
import com.benefitj.spring.mqtt.annotaion.MqttMessageListenerEndpoint;
import com.benefitj.spring.mqtt.annotaion.EnableMqttSubscriber;
import com.benefitj.spring.mqtt.MqttHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * MQTT消息订阅
 */
@EnableAutoApplicationListener
@EnableMqttSubscriber
@SpringBootApplication
public class MqttSubscriberApplication {
  public static void main(String[] args) {
    SpringApplication.run(MqttSubscriberApplication.class, args);
  }


  @Slf4j
  @Component
  public static class DefaultMqttMessageSubscriber {

    @MqttMessageListenerEndpoint(topics = "/device/+")
    public void handleMessage(Message<?> message) throws MessagingException {
      log.info("{}, payload: {}"
          , MqttHeaders.of(message.getHeaders())
          , new String((byte[]) message.getPayload())
      );
    }
  }

  @Slf4j
  @Component
  public static class DefaultMqttMessageSubscriber2 {

    @MqttMessageListenerEndpoint(topics = "/device/010003b8")
    public void handleMessage(Message<?> message) throws MessagingException {
      log.info("{}, payload: {}"
          , MqttHeaders.of(message.getHeaders())
          , new String((byte[]) message.getPayload())
      );
    }
  }
}
