package com.benefitj.examples;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.ratelimiter.EnableRedisRateLimiter;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.listener.OnAppStop;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@EnableRedisMessageChannel
@EnableHttpLoggingHandler       // HTTP请求日志
@EnableRedisRateLimiter         // redis RateLimiter
@EnableEventBusPoster           // eventbus
@EnableAthenapdf                // PDF
@SpringBootApplication
public class SpringbootDoughnutApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringbootDoughnutApplication.class, args);
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onAppState() {
  }

  @OnAppStart
  public void onAppStart() {
    System.err.println("app started ...");
  }

  @OnAppStop
  public void onAppStop() {
    System.err.println("app stopped ...");
  }

}
