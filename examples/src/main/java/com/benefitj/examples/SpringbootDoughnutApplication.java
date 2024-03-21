package com.benefitj.examples;

import com.benefitj.core.EventLoop;
import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.ratelimiter.EnableRedisRateLimiter;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.influxdb.spring.EnableInfluxDB;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageListener;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.LinkedList;
import java.util.List;


@PropertySource(value = "classpath:/swagger-api-info.properties", encoding = "utf-8")
@EnableSwaggerApi
@EnableRedisMessageListener
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

  @EventListener(ApplicationReadyEvent.class)
  public void onAppStart() {
    RedisTemplate<String, Object> redisTemplate = SpringCtxHolder.getBean("redisTemplate");
//    List<String> keys = keys(redisTemplate, "report:doQuartz:*", 1);
    List<String> keys = keys(redisTemplate, "collector:bind_record:*", 1);
    log.info("keys: {}", keys);

    EventLoop.asyncIO(() -> System.exit(0), 1000);
  }
  public List<String> keys(RedisTemplate<String, ?> redisTemplate, String pattern, int size) {
    ScanOptions options = ScanOptions.scanOptions()
        .match(pattern)
        .count(size)
        .build();
    List<String> keys = new LinkedList<>();
    try (Cursor<String> cursor = redisTemplate.scan(options);) {
      while (cursor.hasNext()) {
        keys.add(cursor.next());
        if (keys.size() >= size) {
          break;
        }
      }
    }
    return keys;
  }

}
