package com.benefit.examples;

import com.benefit.aop.EnableAutoAopWebHandler;
import com.benefit.websocket.EnableSpringWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSpringWebSocket // 自定义WebSocket注册
@EnableAutoAopWebHandler // AOP
@SpringBootApplication
public class SpringbootDoughnutApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringbootDoughnutApplication.class, args);
  }
}
