package com.benefitj.spring.redis;

import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.AnnotationResolverImpl;
import com.benefitj.spring.annotation.MetadataHandler;
import com.benefitj.spring.ctx.SpringCtxHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.List;

/**
 * Redis注册器
 */
public class RedisMessageListenerRegistrar extends AnnotationBeanProcessor implements MetadataHandler {

  private RedisMessageListenerContainer container;

  private ApplicationContext context;

  public RedisMessageListenerRegistrar(RedisMessageListenerContainer container) {
    this.container = container;
    this.setMetadataHandler(this);
    this.setResolver(new AnnotationResolverImpl(RedisMessageListener.class));
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      RedisMessageListener rmc = metadata.getFirstAnnotation(RedisMessageListener.class);
      String[] channels = rmc.value();
      if (channels.length <= 0) {
        continue;
      }

      MessageListenerAdapter adapter = createAdapter(metadata);
      for (String channel : channels) {
        if (StringUtils.isBlank(channel)) {
          throw new IllegalArgumentException(
              "redis channel不能为空: " + metadata.getBean().getClass() + "." + metadata.getMethod().getName());
        }
        channel = channel.trim();
        if ((channel.startsWith("${") || channel.startsWith("#{")) && channel.endsWith("}")) {
          channel = SpringCtxHolder.getEnvProperty(channel);
          if (StringUtils.isBlank(channel)) {
            throw new IllegalStateException("redis channel不能为空: " + channel);
          }
          for (String subChannel : channel.split(",")) {
            if (StringUtils.isNoneBlank(subChannel)) {
              container.addMessageListener(adapter, new PatternTopic(subChannel.trim()));
            }
          }
        } else {
          container.addMessageListener(adapter, new PatternTopic(channel));
        }
      }
    }
  }

  /**
   * 创建代理监听的适配器
   *
   * @param metadata 注解的信息
   * @return 返回适配器
   */
  protected MessageListenerAdapter createAdapter(AnnotationMetadata metadata) {
    SimpleMethodInvoker invoker = new SimpleMethodInvoker(metadata.getBean(), metadata.getMethod());
    MessageListenerAdapter adapter = new MessageListenerAdapter();
    // 代理对象
    adapter.setDelegate((MessageListener) (message, pattern) -> invoker.invoke(message, pattern, new String(pattern)));
    // 代理方法
    adapter.setDefaultListenerMethod("onMessage");
    return adapter;
  }

}
