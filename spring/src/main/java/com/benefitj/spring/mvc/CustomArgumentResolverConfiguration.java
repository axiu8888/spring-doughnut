package com.benefitj.spring.mvc;

import com.benefitj.spring.mvc.get.GetBodyArgumentResolver;
import com.benefitj.spring.mvc.page.PageBodyArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CustomArgumentResolverConfiguration {

  /**
   * 自定义参数解析
   */
  @ConditionalOnMissingBean(name = "customArgumentResolverWebMvcConfigurer")
  @Bean("customArgumentResolverWebMvcConfigurer")
  public CustomArgumentResolverWebMvcConfigurer customArgumentResolverWebMvcConfigurer(
      @Autowired(required = false) List<CustomHandlerMethodArgumentResolver> argumentResolvers) {
    return new CustomArgumentResolverWebMvcConfigurer(argumentResolvers);
  }

  /**
   * GET body 解析
   */
  @ConditionalOnMissingBean
  @Bean
  public GetBodyArgumentResolver getBodyArgumentResolver() {
    return new GetBodyArgumentResolver();
  }

  /**
   * 分页参数解析
   */
  @ConditionalOnMissingBean
  @Bean
  public PageBodyArgumentResolver pageBodyArgumentResolver() {
    return new PageBodyArgumentResolver();
  }

}
