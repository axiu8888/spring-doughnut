package com.benefitj.vertxmqtt.subscriber;

import com.benefitj.mqtt.vertx.client.VertxMqttMessageDispatcher;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.vertxmqtt.subscriber.EnableMqttSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@EnableMqttSubscriber
@SpringBootApplication
public class VertxMqttSubscriberApplication {
  public static void main(String[] args) {
    SpringApplication.run(VertxMqttSubscriberApplication.class, args);
  }

  static {
    AppStateHook.registerStart(event -> appStart());
  }

  static void appStart() {
    VertxMqttMessageDispatcher dispatcher = SpringCtxHolder.getBean(VertxMqttMessageDispatcher.class);
    // 订阅
    dispatcher.subscribe("/device/#", (topic, message) ->
        log.info("rcv.1, {}, {}", topic, message.payload().toString()));
    // 订阅
    dispatcher.subscribe("/device/collector/#", (topic, message) ->
        log.info("rcv.2, {}, {}", topic, message.payload().toString()));
  }

}
