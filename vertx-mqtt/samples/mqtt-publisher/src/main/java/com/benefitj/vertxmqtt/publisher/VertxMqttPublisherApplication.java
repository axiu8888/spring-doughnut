package com.benefitj.vertxmqtt.publisher;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.StackLogger;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.vertxmqtt.publisher.EnableMqttPublisher;
import com.benefitj.spring.vertxmqtt.publisher.MqttPublisher;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@EnableMqttPublisher
@SpringBootApplication
public class VertxMqttPublisherApplication {
  public static void main(String[] args) {
    SpringApplication.run(VertxMqttPublisherApplication.class, args);
  }

  static {
    AppStateHook.registerStart(event -> appStart());
  }

  static void appStart() {
    final Logger log = StackLogger.getLogger();
    final EventLoop single = EventLoop.newSingle(false);
    MqttPublisher publisher = SpringCtxHolder.getBean(MqttPublisher.class);
    single.scheduleAtFixedRate(() -> {
      try {
        log.info("发布消息...");
        publisher.publish("/device/collector/" + "010003b8", DateFmtter.fmtNowS());
      } catch (Exception e) {
        log.info("throw: {}", e.getMessage());
      }
    }, 1, 5, TimeUnit.SECONDS);
  }
}
