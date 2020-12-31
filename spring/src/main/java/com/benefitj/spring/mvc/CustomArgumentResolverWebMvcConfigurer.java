package com.benefitj.spring.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 添加自定义参数解析
 */
public class CustomArgumentResolverWebMvcConfigurer implements WebMvcConfigurer {

  @Autowired(required = false)
  private List<CustomHandlerMethodArgumentResolver> argumentResolvers;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.addAll(argumentResolvers);
  }

}
