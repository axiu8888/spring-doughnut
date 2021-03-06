package com.benefitj.system.config;

import com.benefitj.scaffold.quartz.EnableQuartzConfuration;
import com.benefitj.scaffold.spring.EnableScaffoldWebSecurityConfiguration;
import com.benefitj.spring.annotation.AnnotationSearcher;
import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.aop.web.EnableAutoAopWebHandler;
import com.benefitj.spring.eventbus.EnableEventBusPoster;
import com.benefitj.spring.mvc.mapping.MappingAnnotationResolver;
import com.benefitj.spring.redis.EnableRedisMessageChannel;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableSwaggerApi
@EnableAutoAopWebHandler      // AOP
@EnableHttpLoggingHandler     // 打印请求日志
@EnableQuartzConfuration      // quartz
@EnableEventBusPoster         // EventBus
@EnableScaffoldWebSecurityConfiguration // web security
@EnableRedisMessageChannel    // redis
@Configuration
public class WebMvcConfig {

  @Bean("mappingSearcher")
  public AnnotationSearcher mappingSearcher() {
    MappingAnnotationResolver resolver = new MappingAnnotationResolver();
    resolver.addBasePackages("com.*.controller"); // 扫描的包
    return new AnnotationSearcher(resolver);
  }

}
