package com.benefitj.redispublisher;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * redis通道消息发布
 */
@Profile("pub")
@EnableRedisMessageChannel
@SpringBootApplication
public class RedisPublisherApplication {
  public static void main(String[] args) {
    SpringApplication.run(RedisPublisherApplication.class, args);
  }

  static {
    EventLoop.main().execute(() -> {});
    AppStateHook.registerStart(e -> {
      final Logger log = LoggerFactory.getLogger(RedisPublisherApplication.class);
      // 启动主线程，防止程序自动自动退出
      EventLoop.main().execute(() -> log.info("{} start...", SpringCtxHolder.getAppName()));

      // 发送
      StringRedisTemplate redisTemplate = SpringCtxHolder.getBean(StringRedisTemplate.class);
      EventLoop.io().scheduleAtFixedRate(() -> {
        // 发布消息
        String msg = "now: " + DateFmtter.fmtNow();
        log.info("发布消息: " + msg);
        redisTemplate.convertAndSend("channel:test", msg);
      }, 1, 5, TimeUnit.SECONDS);
    });
  }

}
