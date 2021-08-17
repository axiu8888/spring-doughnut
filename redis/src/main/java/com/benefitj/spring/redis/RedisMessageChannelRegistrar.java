package com.benefitj.spring.redis;

import com.benefitj.spring.registrar.AnnotationMetadataRegistrar;
import com.benefitj.spring.registrar.AnnotationMetadata;
import com.benefitj.spring.registrar.MethodElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Redis注册器
 */
public class RedisMessageChannelRegistrar implements AnnotationMetadataRegistrar, ApplicationContextAware {

  private RedisMessageListenerContainer container;

  private ApplicationContext applicationContext;

  public RedisMessageChannelRegistrar(RedisMessageListenerContainer container) {
    this.container = container;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  /**
   * 注册
   *
   * @param metadata    注解类的信息
   * @param beanFactory bean工厂
   */
  @Override
  public void register(AnnotationMetadata metadata, ConfigurableListableBeanFactory beanFactory) {
    Object bean = metadata.getBean();
    ApplicationContext ctx = getApplicationContext();
    for (MethodElement element : metadata.getMethodElements()) {
      RedisMessageChannel rmc = (RedisMessageChannel) element.getAnnotations()[0];
      String[] channels = rmc.value();
      if (channels.length <= 0) {
        continue;
      }

      MessageListenerAdapter adapter = createAdapter(metadata, element, channels);
      for (String channel : channels) {
        if (StringUtils.isBlank(channel)) {
          throw new IllegalArgumentException(
              "redis channel不能为空: " + bean.getClass() + "." + element.getMethod().getName());
        }
        channel = channel.trim();
        if ((channel.startsWith("${") || channel.startsWith("#{")) && channel.endsWith("}")) {
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
              getContainer().addMessageListener(adapter, new PatternTopic(ch.trim()));
            }
          }
        } else {
          PatternTopic topic = new PatternTopic(channel);
          getContainer().addMessageListener(adapter, topic);
        }
      }
    }
  }

  /**
   * 创建代理监听的适配器
   *
   * @param metadata 注解的信息
   * @param channels 通道
   * @return 返回适配器
   */
  protected MessageListenerAdapter createAdapter(AnnotationMetadata metadata,
                                                 MethodElement element,
                                                 String[] channels) {
    RedisMessageListenerAdapter delegate = new RedisMessageListenerAdapter(
        metadata.getBean(), element.getMethod(), channels);
    MessageListenerAdapter adapter = new MessageListenerAdapter(delegate);
    // 代理对象
    adapter.setDelegate(delegate);
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
