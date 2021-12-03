package com.benefitj.mqttpublisher;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.ShutdownHook;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.mqtt.publisher.EnableMqttPublisher;
import com.benefitj.spring.mqtt.publisher.MqttPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * MQTT消息发布
 */
@EnableMqttPublisher
@SpringBootApplication
public class MqttPublisherApplication {
  public static void main(String[] args) {
    SpringApplication.run(MqttPublisherApplication.class, args);
  }

  /**
   * 消息发布
   */
  @Slf4j
  @Component
  public static class Example {

    private final EventLoop single = EventLoop.newSingle(false);

    @Autowired
    private MqttPublisher publisher;

    @OnAppStart
    public void onAppStart() {
      ShutdownHook.register(single::shutdownNow);

      single.scheduleAtFixedRate(() -> {
        try {
          log.info("发布消息...");
          publisher.publish("/device/010003b8", DateFmtter.fmtNowS());
        } catch (Exception e) {
          log.info("throw: {}", e.getMessage());
        }
      }, 1, 5, TimeUnit.SECONDS);
    }

  }
}
