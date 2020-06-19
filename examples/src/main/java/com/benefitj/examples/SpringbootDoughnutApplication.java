package com.benefitj.examples;

import com.benefitj.aop.EnableAutoAopWebHandler;
import com.benefitj.applicationlistener.EnableAutoApplicationListener;
import com.benefitj.websocket.EnableSpringWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSpringWebSocket // 自定义WebSocket注册
@EnableAutoAopWebHandler // AOP
@EnableAutoApplicationListener // 监听事件
@SpringBootApplication
public class SpringbootDoughnutApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringbootDoughnutApplication.class, args);
  }
}
