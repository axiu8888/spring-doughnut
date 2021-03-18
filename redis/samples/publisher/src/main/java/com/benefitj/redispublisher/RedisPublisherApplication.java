package com.benefitj.redispublisher;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis通道消息发布
 */
@EnableRedisMessageChannel
@SpringBootApplication
public class RedisPublisherApplication {
  public static void main(String[] args) {
    SpringApplication.run(RedisPublisherApplication.class, args);
  }


  @Slf4j
  @Component
  public static class RedisMessagePublishExecutor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("#{ @environment['spring.redis.publish-channel'] ?: 'channel:test' }")
    private String publishChannel;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
      EventLoop.io().scheduleAtFixedRate(() -> {
        // 发布消息
        String msg = "now: " + DateFmtter.fmtNow();
        log.info("发布消息: " + msg);
        for (int i = 0; i < 1; i++) {
          redisTemplate.convertAndSend(publishChannel, msg + "___" + i);
        }
      }, 1, 5, TimeUnit.SECONDS);
    }

  }

}
