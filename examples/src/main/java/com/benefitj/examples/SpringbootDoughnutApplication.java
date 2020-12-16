package com.benefitj.examples;

import com.benefitj.spring.aop.log.EnableRequestLoggingHandler;
import com.benefitj.spring.applicationevent.EnableApplicationListener;
import com.benefitj.spring.athenapdf.EnableAthenapdfConfiguration;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.websocket.EnableSpringWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSpringCtxInit
@EnableSpringWebSocket // 自定义WebSocket注册
//@EnableAutoAopWebHandler // AOP
@EnableRequestLoggingHandler
@EnableApplicationListener // 监听事件
@EnableAthenapdfConfiguration // PDF
@EnableEventBusPoster
@SpringBootApplication
public class SpringbootDoughnutApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringbootDoughnutApplication.class, args);
  }
}
