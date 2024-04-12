package com.benefitj.spring.listener;


import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationResolver;
import com.benefitj.spring.annotation.AnnotationResolverImpl;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;
import java.util.List;

/**
 * APP状态注解配置
 */
@EnableSpringCtxInit
@Configuration
public class AutoAppStateConfiguration {

  @ConditionalOnMissingBean
  @Bean
  public AppStateEventAdapter appStateEventAdapter(@Autowired(required = false) List<AppStateListener> listeners) {
    return new AppStateEventAdapter(listeners);
  }

  /**
   * OnAppStart 、 OnAppStop
   */
  @Lazy(value = false)
  @ConditionalOnMissingBean(name = "appStateProcessor")
  @Bean("appStateProcessor")
  public AnnotationBeanProcessor appStateProcessor() {
    AnnotationResolver resolver = new AnnotationResolverImpl(Arrays.asList(OnAppStart.class, OnAppStop.class), false);
    return new AnnotationBeanProcessor(resolver, new AppStateMetadataHandler());
  }

}
