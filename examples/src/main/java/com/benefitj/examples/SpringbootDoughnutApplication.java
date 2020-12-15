package com.benefitj.examples;

import com.alibaba.fastjson.JSON;
import com.benefitj.event.Event;
import com.benefitj.event.RawEvent;
import com.benefitj.spring.aop.log.EnableRequestLoggingHandler;
import com.benefitj.spring.applicationevent.EnableApplicationListener;
import com.benefitj.spring.athenapdf.EnableAthenapdfConfiguration;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.websocket.EnableSpringWebSocket;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

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

//  @Slf4j
//  @Component
//  public static class SimpleEventListener implements ApplicationListener<ApplicationEvent> {
//
//    @Override
//    public void onApplicationEvent(ApplicationEvent event) {
//      log.info("SimpleEventListener_____onApplicationEvent()  {}", event.getClass());
//    }
//  }

  @Slf4j
  @Component
  public static class SimpleEventAdapter {

    @Subscribe
    public void onEvent(RawEvent event) {
      log.info("onEvent: {}, hash: {}", event.getPayload(), Integer.toHexString(event.hashCode()));
    }

    @Subscribe
    public void onEvent2(Event event) {
      log.info("onEvent2: {}, hash: {}", JSON.toJSONString(event), Integer.toHexString(event.hashCode()));
    }
  }
}
