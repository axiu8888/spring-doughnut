package com.benefitj.redissubscriber;

import com.benefitj.core.EventLoop;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import com.benefitj.spring.redis.RedisMessageChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

/**
 * redis通道消息订阅
 */
@Profile("sub")
@EnableRedisMessageChannel
@SpringBootApplication
public class RedisSubscriberApplication {
  public static void main(String[] args) {
    SpringApplication.run(RedisSubscriberApplication.class, args);
  }


  @Slf4j
  @Component
  public static class RedisChannel {

    @OnAppStart
    public void onAppStart() {
      EventLoop.main().execute(() -> log.info("{} start...", SpringCtxHolder.getAppName()));
    }

    @RedisMessageChannel({"${spring.redis.subscribe-channel}"})
    public void onMessage(String pattern, Message message) {
      // 处理消息
      log.info("接收到消息1: {}, pattern: {}"
          , new String(message.getBody())
          , pattern
      );
    }

    @RedisMessageChannel({"${spring.redis.subscribe-channel}"})
    public void onMessage2(String pattern, Message message) {
      // 处理消息
      log.info("接收到消息2, {}, pattern: {}"
          , new String(message.getBody())
          , pattern
      );
    }

  }

}
