package com.benefitj.examples;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.ratelimiter.EnableRedisRateLimiter;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.influxdb.spring.EnableInfluxDB;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@PropertySource(value = "classpath:/swagger-api-info.properties", encoding = "utf-8")
@EnableSwaggerApi
@EnableRedisMessageChannel
@EnableHttpLoggingHandler       // HTTP请求日志
@EnableRedisRateLimiter         // redis RateLimiter
@EnableEventBusPoster           // eventbus
@EnableAthenapdf                // PDF
@EnableInfluxDB                   // InfluxDB
@SpringBootApplication
@Slf4j
public class SpringbootDoughnutApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringbootDoughnutApplication.class, args);
  }

  static {
    AppStateHook.register(
        evt -> log.info("app started ..."),
        evt -> log.info("app stopped ...")
    );
  }

}
