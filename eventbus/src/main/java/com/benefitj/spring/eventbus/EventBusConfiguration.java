package com.benefitj.spring.eventbus;

import com.benefitj.event.EventBusPoster;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(EventBusConfiguration.class)
@Configuration
public class EventBusConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public EventBusPoster poster() {
    return EventBusPoster.getInstance();
  }

  @ConditionalOnMissingBean
  @Bean
  public EventBusAdapterRegister eventBusAdapterRegister(EventBusPoster poster) {
    return new EventBusAdapterRegister(poster);
  }

}
