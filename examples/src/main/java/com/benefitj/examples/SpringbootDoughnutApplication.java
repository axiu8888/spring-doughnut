package com.benefitj.examples;

import com.benefitj.aop.log.EnableRequestLoggingHandler;
import com.benefitj.applicationevent.EnableAutoApplicationListener;
import com.benefitj.spring.eventbus.EnableAutoEventBusPoster;
import com.benefitj.websocket.EnableSpringWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSpringWebSocket // 自定义WebSocket注册
//@EnableAutoAopWebHandler // AOP
@EnableRequestLoggingHandler
@EnableAutoApplicationListener // 监听事件
@EnableAutoEventBusPoster
@SpringBootApplication
public class SpringbootDoughnutApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringbootDoughnutApplication.class, args);
  }
}
