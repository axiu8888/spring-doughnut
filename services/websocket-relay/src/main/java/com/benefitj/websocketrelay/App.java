package com.benefitj.websocketrelay;

import com.benefitj.spring.aop.web.EnableAutoAopWebHandler;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSwaggerApi
@EnableAutoAopWebHandler
@SpringBootApplication
public class App {
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
