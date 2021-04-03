package com.benefitj.athenapdfservice;

import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.athenapdf.EnableAthenapdfConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableHttpLoggingHandler
@EnableAthenapdfConfiguration
@SpringBootApplication
public class AthenapdfApplication {
  public static void main(String[] args) {
    SpringApplication.run(AthenapdfApplication.class, args);
  }
}
