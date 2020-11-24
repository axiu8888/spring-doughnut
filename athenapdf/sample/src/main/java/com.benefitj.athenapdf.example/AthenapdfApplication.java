package com.benefitj.athenapdf.example;

import com.benefitj.athenapdf.spring.EnableAthenapdfConfiguration;
import com.benefitj.spring.aop.log.EnableRequestLoggingHandler;
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
