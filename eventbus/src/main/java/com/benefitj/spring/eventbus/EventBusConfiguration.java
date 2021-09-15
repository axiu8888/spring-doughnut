package com.benefitj.spring.eventbus;

import com.benefitj.event.EventBusPoster;
import com.benefitj.event.EventWrapper;
import com.benefitj.spring.registrar.RegistrarMethodAnnotationBeanPostProcessor;
import com.google.common.eventbus.Subscribe;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(EventBusConfiguration.class)
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
    EventBusPoster poster = EventBusPoster.getInstance();
    poster.setEventWrapper(eventWrapper);
    return poster;
  }

  /**
   * 注解处理器
   */
  @ConditionalOnMissingBean(name = "eventBusAnnotationBeanPostProcessor")
  @Bean("eventBusAnnotationBeanPostProcessor")
  public RegistrarMethodAnnotationBeanPostProcessor eventBusAnnotationBeanPostProcessor(EventBusSubscriberMetadataRegistrar registrar) {
    return new RegistrarMethodAnnotationBeanPostProcessor(registrar, Subscribe.class);
  }

  /**
   * 注解注册器
   */
  @ConditionalOnMissingBean
  @Bean
  public EventBusSubscriberMetadataRegistrar eventBusSubscriberMetadataRegistrar() {
    return new EventBusSubscriberMetadataRegistrar();
  }

  /**
   * adapter工厂
   */
  @ConditionalOnMissingBean
  @Bean
  public DefaultEventBusAdapterFactory eventBusAdapterFactory() {
    return new DefaultEventBusAdapterFactory();
  }

}
