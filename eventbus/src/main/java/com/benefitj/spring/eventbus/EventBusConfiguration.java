package com.benefitj.spring.eventbus;

import com.benefitj.event.EventBusPoster;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(EventBusConfiguration.class)
@ConditionalOnClass(EventBusPoster.class)
@Configuration
public class EventBusConfiguration {

  @ConditionalOnMissingBean(EventBusPoster.class)
  @Bean
  public EventBusPoster poster() {
    return EventBusPoster.getInstance();
  }

  @ConditionalOnMissingBean(EventBusAdapterRegister.class)
  @Bean
  public EventBusAdapterRegister eventBusAdapterRegister(EventBusPoster poster) {
    return new EventBusAdapterRegister(poster);
  }

}
