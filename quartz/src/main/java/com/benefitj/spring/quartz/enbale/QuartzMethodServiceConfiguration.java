package com.benefitj.spring.quartz.enbale;

import org.springframework.context.annotation.Configuration;

/**
 * Quartz服务注册
 */
@EnableQuartz
@Configuration
public class QuartzMethodServiceConfiguration {

//  /**
//   * 注册
//   */
//  @ConditionalOnMissingBean
//  @Bean
//  public QuartzServiceRegistrar quartzServiceRegistrar() {
//    return new QuartzServiceRegistrar();
//  }
//
//  /**
//   * Quartz方法注解的后置处理器
//   */
//  @ConditionalOnMissingBean(name = "quartzServiceProcessor")
//  @Bean("quartzServiceProcessor")
//  public RegistrarMethodAnnotationBeanPostProcessor quartzServiceProcessor(QuartzServiceRegistrar registrar) {
//    return new RegistrarMethodAnnotationBeanPostProcessor(registrar, QuartzService.class);
//  }

}
