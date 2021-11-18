package com.benefitj.spring.redis;

import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.spring.annotationprcoessor.AnnotationBeanProcessor;
import com.benefitj.spring.annotationprcoessor.AnnotationMetadata;
import com.benefitj.spring.annotationprcoessor.MetadataHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.List;

/**
 * Redis注册器
 */
public class RedisMessageChannelRegistrar extends AnnotationBeanProcessor
    implements MetadataHandler, ApplicationContextAware {

  private RedisMessageListenerContainer container;

  private ApplicationContext context;

  public RedisMessageChannelRegistrar(RedisMessageListenerContainer container) {
    this.container = container;
    this.setAnnotationType(RedisMessageChannel.class);
    this.setMetadataHandler(this);
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }

  public ApplicationContext getContext() {
    return context;
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      RedisMessageChannel rmc = (RedisMessageChannel) metadata.getAnnotation();
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
          StringBuilder sb = new StringBuilder(channel);
          sb.delete(0, 2);
          sb.delete(sb.length() - 1, sb.length());
          channel = getContext().getEnvironment().getProperty(sb.toString());
          if (StringUtils.isBlank(channel)) {
            throw new IllegalStateException("redis channel不能为空: " + sb);
          }
          String[] split = channel.split(",");
          for (String ch : split) {
            if (StringUtils.isNoneBlank(ch)) {
              getContainer().addMessageListener(adapter, new PatternTopic(ch.trim()));
            }
          }
        } else {
          getContainer().addMessageListener(adapter, new PatternTopic(channel));
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
    adapter.setDelegate((MessageListener) (message, pattern)
        -> invoker.invoke(message, pattern, new String(pattern)));
    // 代理方法
    adapter.setDefaultListenerMethod("onMessage");
    return adapter;
  }

  public RedisMessageListenerContainer getContainer() {
    return container;
  }

  public void setContainer(RedisMessageListenerContainer container) {
    this.container = container;
  }

}
