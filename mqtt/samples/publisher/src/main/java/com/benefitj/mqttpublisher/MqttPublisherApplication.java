package com.benefitj.mqttpublisher;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.ShutdownHook;
import com.benefitj.spring.listener.EnableAppStateListener;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.mqtt.event.EnableEventPublisher;
import com.benefitj.spring.mqtt.event.EventPublisher;
import com.benefitj.spring.mqtt.event.EventTopic;
import com.benefitj.spring.mqtt.event.TopicPlaceholder;
import com.benefitj.spring.mqtt.publisher.EnableMqttPublisher;
import com.benefitj.spring.mqtt.publisher.MqttPublisher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * MQTT消息发布
 */
@EnableAppStateListener
@EnableEventPublisher
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

    @Autowired
    private EventPublisher eventPublisher;

    @OnAppStart
    public void onAppStart() {
      ShutdownHook.register(single::shutdownNow);

      single.scheduleAtFixedRate(() -> {
        try {
          log.info("发布消息...");
          publisher.publish("/device/010003b8", DateFmtter.fmtNowS());

          eventPublisher.publish(BindEvent.builder()
              .deviceId("01000345")
              .type("bind")
              .flag("ABC")
              .message(DateFmtter.fmtNowS())
              .build());
        } catch (Exception e) {
          log.info("throw: {}", e.getMessage());
        }
      }, 1, 5, TimeUnit.SECONDS);
    }

  }

  @SuperBuilder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  @EventTopic("/event/#{deviceId}/#{type}/#{flag2}")
  //@EventTopic("/event/#{deviceId}/#{type}")
  public static class BindEvent {
    /**
     * 设备ID
     */
    @TopicPlaceholder(name = "deviceId")
    private String deviceId;
    /**
     * 类型
     */
    @TopicPlaceholder(name = "type")
    private String type;
    /**
     * 标记
     */
    @TopicPlaceholder(name = "flag2")
    private String flag;
    /**
     * 消息
     */
    private String message;

  }

}
