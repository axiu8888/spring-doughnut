package com.benefitj.vertxmqtt.subscriber;

import com.benefitj.core.EventLoop;
import com.benefitj.core.HexUtils;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.vertxmqtt.subscriber.EnableMqttSubscriber;
import com.benefitj.vertx.mqtt.client.VertxMqttClient;
import com.benefitj.vertx.mqtt.client.VertxMqttMessageDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@Slf4j
@EnableMqttSubscriber
@SpringBootApplication
public class VertxMqttSubscriberApplication {
  public static void main(String[] args) {
    SpringApplication.run(VertxMqttSubscriberApplication.class, args);
  }

  static {
    AppStateHook.registerStart(event -> EventLoop.asyncIO(VertxMqttSubscriberApplication::appStart, 1, TimeUnit.SECONDS));
  }

  static void appStart() {
    log.info("订阅数据...");
    VertxMqttMessageDispatcher dispatcher = SpringCtxHolder.getBean(VertxMqttMessageDispatcher.class);
    VertxMqttClient client = dispatcher.getClient();
    log.info("{} 是否已连接: {}, {}"
        , client.getClientId()
        , client.isConnected()
        , client.getHost() + ":" + client.getPort()
    );
    // 订阅
    dispatcher.subscribe("/device/#", (topic, message) ->
        log.info("rcv.1, {}, {}", topic, message.payload().toString()));
    // 订阅
    dispatcher.subscribe("/device/collector/#", (topic, message) ->
        log.info("rcv.2, {}, {}", topic, message.payload().toString()));
    dispatcher.subscribe("hardware/#", (topic, message) ->
        log.info("rcv.3, {}, {}", topic, HexUtils.bytesToHex(message.payload().getBytes())));
  }

}
