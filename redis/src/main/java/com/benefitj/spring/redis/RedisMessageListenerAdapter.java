package com.benefitj.spring.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * redis消息监听适配器
 */
public class RedisMessageListenerAdapter implements MessageListener {

  private Object bean;
  private Method method;
  private String[] channels;

  public RedisMessageListenerAdapter() {
  }

  public RedisMessageListenerAdapter(Object bean, Method method, String[] channels) {
    this.bean = bean;
    this.method = method;
    this.channels = channels;
  }

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      ReflectionUtils.invokeMethod(getMethod(), getBean(), message, pattern);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException(String.format(
          "Redis监听方法错误，参数必须是\"%s message\"和\"byte[] pattern\"", Message.class.getName()));
    }
  }

  public Object getBean() {
    return bean;
  }

  public void setBean(Object bean) {
    this.bean = bean;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public String[] getChannels() {
    return channels;
  }

  public void setChannels(String[] channels) {
    this.channels = channels;
  }
}
