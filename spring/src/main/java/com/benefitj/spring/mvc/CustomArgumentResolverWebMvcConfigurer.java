package com.benefitj.spring.mvc;

import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 添加自定义参数解析
 */
public class CustomArgumentResolverWebMvcConfigurer implements WebMvcConfigurer {

  private List<CustomHandlerMethodArgumentResolver> argumentResolvers;

  public CustomArgumentResolverWebMvcConfigurer() {
  }

  public CustomArgumentResolverWebMvcConfigurer(List<CustomHandlerMethodArgumentResolver> argumentResolvers) {
    this.argumentResolvers = argumentResolvers;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.addAll(getArgumentResolvers());
  }

  public List<CustomHandlerMethodArgumentResolver> getArgumentResolvers() {
    return argumentResolvers;
  }

  public void setArgumentResolvers(List<CustomHandlerMethodArgumentResolver> argumentResolvers) {
    this.argumentResolvers = argumentResolvers;
  }
}
