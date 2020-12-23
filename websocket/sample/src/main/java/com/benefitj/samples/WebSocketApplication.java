package com.benefitj.samples;

import com.benefitj.spring.websocket.EnableSpringWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSpringWebSocket
@SpringBootApplication
public class WebSocketApplication {
  public static void main(String[] args) {
    SpringApplication.run(WebSocketApplication.class, args);
  }
}
