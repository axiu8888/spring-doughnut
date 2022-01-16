package com.benefitj.system.config;

import com.benefitj.scaffold.quartz.EnableQuartzConfuration;
import com.benefitj.scaffold.spring.EnableScaffoldWebSecurityConfiguration;
import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.web.EnableAutoAopWebHandler;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.mvc.mapping.MappingAnnotationBeanProcessor;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@EnableSwaggerApi
@PropertySource(value = "classpath:swagger-api-info.properties", encoding = "UTF-8")
@EnableAutoAopWebHandler      // AOP
@EnableHttpLoggingHandler     // 打印请求日志
@EnableQuartzConfuration      // quartz
@EnableEventBusPoster         // EventBus
@EnableScaffoldWebSecurityConfiguration // web security
@EnableRedisMessageChannel    // redis
@Configuration
public class WebMvcConfig {

  @Bean
  public MappingAnnotationBeanProcessor requestMappingSearcher() {
    MappingAnnotationBeanProcessor searcher = new MappingAnnotationBeanProcessor();
    // 扫描的包
    searcher.addBasePackages("com.*.controller");
    return searcher;
  }

}
