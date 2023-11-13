package com.benefitj.fileserver.config;


import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.mvc.EnableCustomArgumentResolverWebMvcConfigurer;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * swagger
 */
@PropertySource(value = "classpath:swagger-api-info.properties", encoding = "UTF-8")
@EnableKnife4j
@EnableSwaggerApi
@EnableHttpLoggingHandler
@EnableCustomArgumentResolverWebMvcConfigurer
@Configuration
public class WebConfig {
}
