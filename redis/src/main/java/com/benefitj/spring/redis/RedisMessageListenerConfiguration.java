package com.benefitj.spring.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * redis配置监听配置
 */
@Configuration
public class RedisMessageListenerConfiguration {

  // channel:test

  @ConditionalOnMissingBean
  @Bean
  public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    // key 序列化
    template.setKeySerializer(new StringRedisSerializer());

    Jackson2JsonRedisSerializer serializer = jacksonSerializer();
    // value 序列化
    template.setValueSerializer(serializer);
    // hash value序列化
    template.setHashValueSerializer(serializer);
    return template;
  }

  @ConditionalOnMissingBean
  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    return container;
  }

  /**
   * 监听器注册
   */
  @ConditionalOnMissingBean
  @Bean
  public RedisMessageListenerRegistrar redisMessageListenerRegistrar(RedisMessageListenerContainer container) {
    return new RedisMessageListenerRegistrar(container);
  }

  /**
   * 创建缓存缓存配置管理器
   */
  public static CacheManager newCacheManager(LettuceConnectionFactory factory, Duration defaultTtl, Map<String, RedisCacheConfiguration> cacheConfigurations) {
    return RedisCacheManager.builder(new DefaultRedisCacheWriter(factory, Duration.ofMillis(50L),
            CacheStatisticsCollector.none(), BatchStrategies.keys()))
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(defaultTtl)
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jacksonSerializer()))
            .disableCachingNullValues())
        .withInitialCacheConfigurations(cacheConfigurations)
        .transactionAware()
        .build();
  }


  public static Jackson2JsonRedisSerializer jacksonSerializer() {
    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    return jackson2JsonRedisSerializer;
  }

}
