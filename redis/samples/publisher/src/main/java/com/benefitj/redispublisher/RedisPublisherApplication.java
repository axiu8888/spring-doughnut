package com.benefitj.redispublisher;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.spring.applicationevent.EnableAutoApplicationListener;
import com.benefitj.spring.applicationevent.IApplicationReadyEventListener;
import com.benefitj.spring.redis.EnableRedisMessageChannelConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redis通道消息发布
 */
@EnableAutoApplicationListener
@EnableRedisMessageChannelConfiguration
@SpringBootApplication
public class RedisPublisherApplication {
  public static void main(String[] args) {
    SpringApplication.run(RedisPublisherApplication.class, args);
  }


  private static final EventLoop single = EventLoop.newSingle(false);

  static {
    single.execute(() -> System.err.println("启动..."));
  }

  @Slf4j
  @Component
  public static class RedisMessagePublishExecutor implements IApplicationReadyEventListener {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("#{ @environment['spring.redis.publish-channel'] ?: 'channel:test' }")
    private String publishChannel;

    @Override
    public void onApplicationReadyEvent(ApplicationReadyEvent applicationReadyEvent) {
      single.scheduleAtFixedRate(() -> {
        // 发布消息
        String msg = "now: " + DateFmtter.fmtNow();
        redisTemplate.convertAndSend(publishChannel, msg);
        log.info("发布消息: "+ msg);
      }, 1, 5, TimeUnit.SECONDS);
    }

  }

}
