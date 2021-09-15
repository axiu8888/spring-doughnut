package com.benefitj.athenapdfservice;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:/swagger-api-info.properties", encoding = "utf-8")
@EnableSwaggerApi
@EnableHttpLoggingHandler
@EnableAthenapdf
@SpringBootApplication
public class AthenapdfApplication {
  public static void main(String[] args) {
    SpringApplication.run(AthenapdfApplication.class, args);
  }
}
