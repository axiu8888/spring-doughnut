package com.benefitj.spring.quartzservice;

import com.benefitj.spring.registrar.RegistrarMethodAnnotationBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz服务注册
 */
@Configuration
public class QuartzServiceConfiguration {

  /**
   * 注册
   */
  @ConditionalOnMissingBean
  @Bean
  public QuartzServiceRegistrar quartzServiceRegistrar() {
    return new QuartzServiceRegistrar();
  }

  /**
   * Quartz方法注解的后置处理器
   */
  @ConditionalOnMissingBean(name = "quartzServiceProcessor")
  @Bean("quartzServiceProcessor")
  public RegistrarMethodAnnotationBeanPostProcessor quartzServiceProcessor(QuartzServiceRegistrar registrar) {
    return new RegistrarMethodAnnotationBeanPostProcessor(registrar, QuartzService.class);
  }

}
