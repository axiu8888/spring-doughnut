package com.benefitj.redispublisher;

import com.benefitj.core.EventLoop;
import com.benefitj.spring.redis.EnableRedisMessageChannelConfiguration;
import com.benefitj.spring.redis.RedisMessageChannel;
import com.benefitj.spring.redis.RedisMessageListener;
import com.benefitj.spring.applicationevent.EnableApplicationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

/**
 * redis通道消息订阅
 */
@EnableApplicationListener
@EnableRedisMessageChannelConfiguration
@SpringBootApplication
public class RedisSubscriberApplication {
  public static void main(String[] args) {
    SpringApplication.run(RedisSubscriberApplication.class, args);
  }


  private static final EventLoop single = EventLoop.newSingle(false);

  static {
    single.execute(() -> System.err.println("启动..."));
  }

  @Slf4j
  @Component
//  @RedisMessageChannel({"channel:test", "channel:test2"})
  @RedisMessageChannel({" ${spring.redis.subscribe-channel}"})
  public static class RedisChannel implements RedisMessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
      single.execute(() -> {
        // 处理消息
        log.info("接收到消息: {}, pattern: {}"
            , new String(message.getBody())
            , new String(pattern)
        );
      });
    }
  }

}
