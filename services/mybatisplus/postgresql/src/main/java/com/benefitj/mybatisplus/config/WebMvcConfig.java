package com.benefitj.mybatisplus.config;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.mvc.EnableCustomArgumentResolverWebMvcConfigurer;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//@EnableQuartz // quartz
//@EnableJwtSecurityConfiguration // JWT
@EnableSwaggerApi
@EnableCustomArgumentResolverWebMvcConfigurer
@EnableHttpLoggingHandler
@PropertySource(value = "classpath:swagger-api-info.properties", encoding = "UTF-8")
@Configuration
public class WebMvcConfig {
}
