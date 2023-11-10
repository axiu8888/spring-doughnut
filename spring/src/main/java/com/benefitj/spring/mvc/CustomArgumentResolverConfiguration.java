package com.benefitj.spring.mvc;

import com.benefitj.spring.mvc.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConditionalOnWebApplication
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
   * Query参数解析
   */
  @ConditionalOnMissingBean(name = "queryBodyArgumentResolver")
  @Bean("queryBodyArgumentResolver")
  public QueryBodyArgumentResolver queryBodyArgumentResolver() {
    return new QueryBodyArgumentResolver(QueryRequest.class, QueryBody.class);
  }

  /**
   * 分页参数解析
   */
  @ConditionalOnMissingBean(name = "pageBodyArgumentResolver")
  @Bean("pageBodyArgumentResolver")
  public QueryBodyArgumentResolver pageBodyArgumentResolver() {
    return new QueryBodyArgumentResolver(PageRequest.class, PageBody.class);
  }

}
