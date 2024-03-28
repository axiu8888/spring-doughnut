package com.benefitj.spring.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * Redis分布式锁
 */
@Configuration
public class RedisLockConfiguration {

  @ConditionalOnMissingBean
  @Bean(destroyMethod = "destroy")
  public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
    return new RedisLockRegistry(redisConnectionFactory, "lock");
  }

  @ConditionalOnMissingBean
  @Bean
  public RedisLock redisLockService(RedisLockRegistry redisLockRegistry) {
    return new RedisLock(redisLockRegistry);
  }

}
