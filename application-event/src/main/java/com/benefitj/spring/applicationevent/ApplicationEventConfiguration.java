package com.benefitj.spring.applicationevent;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Lazy
@Configuration
public class ApplicationEventConfiguration {

  /**
   * 事件代理
   */
  @ConditionalOnMissingBean
  @Bean
  public ApplicationEventAdapterHandler applicationEventAdapterHandler() {
    return new ApplicationEventAdapterHandler();
  }

  /**
   * 注解处理器
   */
  @Order(Ordered.LOWEST_PRECEDENCE)
  @ConditionalOnMissingBean
  @Bean
  public ApplicationListenerAnnotationBeanPostProcessor applicationListenerAnnotationBeanPostProcessor(ApplicationEventAdapterHandler applicationEventAdapterHandler) {
    return new ApplicationListenerAnnotationBeanPostProcessor(applicationEventAdapterHandler);
  }

}
