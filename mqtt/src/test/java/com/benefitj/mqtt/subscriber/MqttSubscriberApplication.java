package com.benefitj.mqtt.subscriber;

import com.benefitj.mqtt.paho.MqttCallbackDispatcher;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.mqtt.subscriber.EnableMqttSubscriber;
import com.benefitj.spring.mqtt.subscriber.MqttMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * MQTT消息订阅
 */
@Slf4j
@PropertySource(value = {"classpath:application-sub.properties"}, encoding = "utf-8")
@EnableMqttSubscriber
@SpringBootApplication
public class MqttSubscriberApplication {
  public static void main(String[] args) {
    SpringApplication.run(MqttSubscriberApplication.class, args);
  }

  static {
    AppStateHook.registerStart((evt) -> log.info("app start..."));
    AppStateHook.registerStop((evt) -> log.info("app stop..."));
  }

  @Component
  public static class Example {

    @Autowired
    private MqttCallbackDispatcher dispatcher;

    @MqttMessageListener(
        topics = "/device/#",
        clientIdPrefix = "mqtt-subscriber-",
        serverURI = "spring.mqtt.custom.serverURIs"
    )
    public void onMessage(String topic, byte[] payload) {
      log.info("1. {}, payload: {}", topic, new String(payload));
    }

    @OnAppStart
    public void onStart() {
      dispatcher.subscribe("/event/+/+/+", (topic, msg) -> {
        // 接收到消息
        log.info("2.2 {}, payload: {}", topic, new String(msg.getPayload()));
      });
    }

  }

}
