package com.benefitj.examples;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.listener.EnableAppStateListener;
import com.benefitj.spring.websocket.EnableSpringWebSocket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAppStateListener
@EnableSpringCtxInit
@EnableSpringWebSocket          // 自定义WebSocket注册
//@EnableAutoAopWebHandler        // AOP
@EnableHttpLoggingHandler       // HTTP请求日志
@EnableEventBusPoster           // eventbus
@EnableAthenapdf   // PDF
//@EnableQuartz                   // Quartz
@SpringBootApplication
public class SpringbootDoughnutApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringbootDoughnutApplication.class, args);
  }


  static {
    // app start: do something...
    AppStateHook.registerStart(event -> System.err.println("app start ..."));
    // app stop: do something...
    AppStateHook.registerStop(event -> System.err.println("app stop ..."));
  }

}
