package com.benefitj.examples;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.ratelimiter.EnableRedisRateLimiter;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

  static {
    AppStateHook.register(
        event -> System.err.println("app started ..."),
        event -> System.err.println("app stopped ...")
    );
  }

}
