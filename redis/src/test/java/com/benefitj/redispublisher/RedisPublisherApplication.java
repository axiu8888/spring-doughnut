package com.benefitj.redispublisher;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * redis通道消息发布
 */
@PropertySource(value = {"classpath:application-pub.properties"}, encoding = "utf-8")
@EnableRedisMessageListener
@SpringBootApplication
@Slf4j
public class RedisPublisherApplication {
  public static void main(String[] args) {
    SpringApplication.run(RedisPublisherApplication.class, args);
  }

  static {
    AppStateHook.registerStart(e -> {
      // 启动主线程，防止程序自动自动退出
      EventLoop.main().execute(() -> log.info("{} start...", SpringCtxHolder.getAppName()));

      // 发送
      StringRedisTemplate redisTemplate = SpringCtxHolder.getBean(StringRedisTemplate.class);
      EventLoop.asyncIOFixedRate(() -> {
        // 发布消息
        String msg = "now: " + DateFmtter.fmtNow();
        log.info("发布消息: " + msg);
        redisTemplate.convertAndSend("channel:test", msg);
      }, 1, 5, TimeUnit.SECONDS);
    });
  }

}
