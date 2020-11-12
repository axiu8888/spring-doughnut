package com.benefitj.spring.redis;

import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Redis消息适配器
 */
public class RedisMessageListenerAdapter extends MessageListenerAdapter {

  private String[] channels;

  public RedisMessageListenerAdapter() {
  }

  public RedisMessageListenerAdapter(String[] channels) {
    this.channels = channels;
  }

  public RedisMessageListenerAdapter(Object delegate, String[] channels) {
    super(delegate);
    this.channels = channels;
  }

  public RedisMessageListenerAdapter(Object delegate, String defaultListenerMethod, String[] channels) {
    super(delegate, defaultListenerMethod);
    this.channels = channels;
  }

  public String[] getChannels() {
    return channels;
  }

  public void setChannels(String[] channels) {
    this.channels = channels;
  }

}
