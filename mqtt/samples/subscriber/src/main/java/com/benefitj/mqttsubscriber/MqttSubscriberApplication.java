package com.benefitj.mqttsubscriber;

import com.benefitj.mqtt.paho.MqttCallbackDispatcher;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.listener.OnAppStop;
import com.benefitj.spring.mqtt.subscriber.EnableMqttSubscriber;
import com.benefitj.spring.mqtt.subscriber.MqttMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

/**
 * MQTT消息订阅
 */
@EnableMqttSubscriber
@SpringBootApplication
public class MqttSubscriberApplication {
  public static void main(String[] args) {
    SpringApplication.run(MqttSubscriberApplication.class, args);
  }

  @Slf4j
  @Component
  public static class Example {

    @MqttMessageListener(
        topics = "/device/#",
        clientIdPrefix = "mqtt-subscriber-"
    )
    public void onMessage(String topic, byte[] payload) {
      log.info("1. {}, payload: {}", topic, new String(payload));
    }
  }


  @Slf4j
  @Component
  public static class Example2 {

    @Autowired
    private MqttCallbackDispatcher dispatcher;

    @OnAppStart
    public void onStart() {
      dispatcher.subscribe("/device/#", (topic, msg) -> {
        // 接收到消息
        log.info("2. {}, payload: {}", topic, new String(msg.getPayload()));
      });
    }

    @OnAppStop
    public void onStop() {
    }

  }


}
