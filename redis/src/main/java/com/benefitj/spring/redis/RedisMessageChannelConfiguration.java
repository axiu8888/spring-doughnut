package com.benefitj.spring.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * redis配置
 */
@Configuration
public class RedisMessageChannelConfiguration {

  // channel:test

  @ConditionalOnMissingBean
  @Bean
  public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);

    // key 序列化
    template.setKeySerializer(new StringRedisSerializer());

    Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    serializer.setObjectMapper(om);
    // value 序列化
    template.setValueSerializer(serializer);
    // hash value序列化
    template.setHashValueSerializer(serializer);
    return template;
  }

  @ConditionalOnMissingBean
  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                     ApplicationContext ctx) {

    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    registerMessageListener(container, ctx);
    return container;
  }

  /**
   * 注册
   */
  public void registerMessageListener(RedisMessageListenerContainer container, ApplicationContext ctx) {
    Map<String, RedisMessageListener> beans = ctx.getBeansOfType(RedisMessageListener.class);
    List<RedisMessageListener> listeners = beans.values()
        .stream()
        .filter(listener -> listener.getClass().isAnnotationPresent(RedisMessageChannel.class))
        .collect(Collectors.toList());
    // 注册通道
    for (RedisMessageListener listener : listeners) {
      RedisMessageChannel rmc = listener.getClass().getAnnotation(RedisMessageChannel.class);
      String[] channels = rmc.value();
      if (channels.length > 0) {
        RedisMessageListenerAdapter adapter = newListenerAdapter(listener, channels);
        for (String channel : channels) {
          if (StringUtils.isBlank(channel)) {
            throw new IllegalArgumentException("redis channel不能为空: " + listener.getClass());
          }
          channel = channel.trim();
          if((channel.startsWith("${") || channel.startsWith("#{")) && channel.endsWith("}")) {
            StringBuilder sb = new StringBuilder(channel);
            sb.delete(0, 2);
            sb.delete(sb.length() - 1, sb.length());
            channel = ctx.getEnvironment().getProperty(sb.toString());
            if (StringUtils.isBlank(channel)) {
              throw new IllegalStateException("redis channel不能为空: " + sb.toString());
            }
            String[] split = channel.split(",");
            for (String ch : split) {
              if (StringUtils.isNoneBlank(ch)) {
                PatternTopic topic = new PatternTopic(ch.trim());
                container.addMessageListener(adapter, topic);
              }
            }
          } else {
            PatternTopic topic = new PatternTopic(channel);
            container.addMessageListener(adapter, topic);
          }
        }
      }
    }
  }

  /**
   * 创建代理监听的适配器
   *
   * @param listener 监听
   * @param channels 通道
   * @return 返回适配器
   */
  protected RedisMessageListenerAdapter newListenerAdapter(RedisMessageListener listener, String[] channels) {
    RedisMessageListenerAdapter adapter = new RedisMessageListenerAdapter();
    // 代理对象
    adapter.setDelegate(listener);
    // 代理方法
    adapter.setDefaultListenerMethod("onMessage");
    // 通道
    adapter.setChannels(channels);
    return adapter;
  }


}
