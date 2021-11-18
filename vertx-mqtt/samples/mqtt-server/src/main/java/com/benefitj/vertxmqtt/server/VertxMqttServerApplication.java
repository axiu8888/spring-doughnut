package com.benefitj.vertxmqtt.server;

import com.benefitj.spring.vertxmqtt.server.EnableMqttServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MQTT消息发布
 */
@EnableMqttServer
@SpringBootApplication
public class VertxMqttServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(VertxMqttServerApplication.class, args);
  }
}
