package com.benefitj.vertxmqtt.server;

import com.benefitj.spring.vertxmqtt.server.EnableMqttServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * MQTT消息发布
 */
@PropertySource(value = {"classpath:version.properties"}, encoding = "utf-8")
@EnableMqttServer
@SpringBootApplication
public class VertxMqttServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(VertxMqttServerApplication.class, args);
  }
}
