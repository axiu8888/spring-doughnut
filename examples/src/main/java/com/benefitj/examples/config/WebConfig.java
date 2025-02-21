package com.benefitj.examples.config;


import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.mvc.EnableCustomArgumentResolverWebMvcConfigurer;
import com.benefitj.spring.mvc.jsonbody.JsonBodyMappingSearcher;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@EnableCaching
@EnableCustomArgumentResolverWebMvcConfigurer
@EnableSwaggerApi
//@EnableRedisRateLimiter         // redis RateLimiter
@EnableHttpLoggingHandler       // HTTP请求日志
@Configuration
public class WebConfig {

  @Bean
  public BodyWrappedFilter bodyWrappedFilter(JsonBodyMappingSearcher searcher) {
    return new BodyWrappedFilter(searcher);
  }
}
