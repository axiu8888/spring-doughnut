package com.benefitj.mybatisplus.config;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.mvc.EnableCustomArgumentResolverWebMvcConfigurer;
import com.benefitj.spring.quartz.EnableQuartz;
import com.benefitj.spring.security.jwt.EnableJwtSecurityConfiguration;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@EnableQuartz // quartz
@EnableJwtSecurityConfiguration // JWT
@EnableSwaggerApi
@PropertySource(value = "classpath:swagger-api-info.properties", encoding = "UTF-8")
@EnableHttpLoggingHandler
@EnableCustomArgumentResolverWebMvcConfigurer
@Configuration
public class WebMvcConfig {
}
