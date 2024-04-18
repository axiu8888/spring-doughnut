package com.benefitj.provider.mqttaclredis;

import com.benefitj.core.EventLoop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;

import java.util.concurrent.TimeUnit;


@SpringBootApplication
public class MqttAclRedisApplication {
  public static void main(String[] args) {
    SpringApplication.run(MqttAclRedisApplication.class, args);
  }


  @EventListener
  public void onAppStart(ApplicationReadyEvent event) {
    ConfigurableApplicationContext ctx = event.getApplicationContext();
    MqttAclStringRedisTemplate redisTemplate = ctx.getBean(MqttAclStringRedisTemplate.class);

    // 结束进程
    EventLoop.asyncIO(() -> {

//      String username = "test2";
//      redisTemplate.deleteUsername(username);
//      redisTemplate.deleteAcl(username);
      System.exit(0);

    }, 10, TimeUnit.SECONDS);
  }

}
