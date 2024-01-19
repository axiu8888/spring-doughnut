package com.benefitj.spring.eventbus;

import com.benefitj.event.EventBusPoster;
import com.benefitj.event.EventWrapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public NameEventWrapper eventWrapper() {
    return new NameEventWrapper();
  }

  /**
   * EventBusPoster
   */
  @ConditionalOnMissingBean
  @Bean
  public EventBusPoster eventBusPoster(EventWrapper eventWrapper) {
    EventBusPoster poster = EventBusPoster.get();
    poster.setEventWrapper(eventWrapper);
    return poster;
  }

  /**
   * 注解注册器
   */
  @ConditionalOnMissingBean
  @Bean
  public EventBusSubscriberRegistrar eventBusSubscriberRegistrar(EventBusPoster poster) {
    return new EventBusSubscriberRegistrar(poster);
  }

}
