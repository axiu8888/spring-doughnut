package com.benefitj.athenapdf.example;

import com.benefitj.athenapdf.spring.EnableAthenapdfConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAthenapdfConfiguration
@SpringBootApplication
public class AthenapdfApplication {
  public static void main(String[] args) {
    SpringApplication.run(AthenapdfApplication.class, args);
  }
}
