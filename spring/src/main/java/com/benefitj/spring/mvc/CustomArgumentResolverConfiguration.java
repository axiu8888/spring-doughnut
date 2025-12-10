package com.benefitj.spring.mvc;

import com.benefitj.spring.mvc.jsonbody.JsonBodyMappingSearcher;
import com.benefitj.spring.mvc.jsonbody.JsonBodyProcessor;
import com.benefitj.spring.mvc.jsonparam.JsonParamArgumentResolver;
import com.benefitj.spring.mvc.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

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

  /**
   * json请求 参数解析
   */
  @Primary
  @ConditionalOnMissingBean(name = "jsonParamArgumentResolver")
  @Bean("jsonParamArgumentResolver")
  public JsonParamArgumentResolver jsonParamArgumentResolver() {
    return new JsonParamArgumentResolver();
  }

  /**
   * json请求解析
   */
  @ConditionalOnMissingBean(name = "jsonBodyMappingSearcher")
  @Bean("jsonBodyMappingSearcher")
  public JsonBodyMappingSearcher jsonBodyMappingSearcher() {
    return new JsonBodyMappingSearcher();
  }

  /**
   * json请求解析
   */
  @Primary
  @ConditionalOnMissingBean(name = "jsonBodyProcessor")
  @Bean("jsonBodyProcessor")
  public JsonBodyProcessor jsonBodyProcessor() {
    return JsonBodyProcessor::toJson;
  }

}
