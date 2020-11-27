package com.benefitj.athenapdf.example;

import com.benefitj.spring.aop.log.EnableRequestLoggingHandler;
import com.benefitj.spring.athenapdf.EnableAthenapdfConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRequestLoggingHandler
@EnableAthenapdfConfiguration
@SpringBootApplication
public class AthenapdfApplication {
  public static void main(String[] args) {
    SpringApplication.run(AthenapdfApplication.class, args);
  }
}
