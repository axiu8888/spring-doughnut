package com.benefitj.mqttsubscriber;

import com.benefitj.spring.applicationevent.EnableApplicationListener;
import com.benefitj.spring.mqtt.MqttHeaders;
import com.benefitj.spring.mqtt.MqttMessageListener;
import com.benefitj.spring.mqtt.config.EnableMqttSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * MQTT消息订阅
 */
@EnableApplicationListener
@EnableMqttSubscriber
@SpringBootApplication
public class MqttSubscriberApplication {
  public static void main(String[] args) {
    SpringApplication.run(MqttSubscriberApplication.class, args);
  }

  @Slf4j
  @Component
  public static class DefaultMqttMessageSubscriber {

    @MqttMessageListener(topics = "/device/+", clientIdPrefix = "mqtt-subscriber-")
    public void handleMessage(Message<?> message) throws MessagingException {
      log.info("{}, payload: {}"
          , MqttHeaders.of(message.getHeaders())
          , new String((byte[]) message.getPayload())
      );
    }
  }

//  @Slf4j
//  @Component
//  public static class DefaultMqttMessageSubscriber2 {
//
//    @MqttMessageListener(topics = "/device/010003b8", clientIdPrefix = "handleMessage-")
//    public void handleMessage(Message<?> message) throws MessagingException {
//      log.info("{}, payload: {}"
//          , MqttHeaders.of(message.getHeaders())
//          , new String((byte[]) message.getPayload())
//      );
//    }
//  }
}
