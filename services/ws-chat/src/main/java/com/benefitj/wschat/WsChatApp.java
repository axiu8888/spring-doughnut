package com.benefitj.wschat;

import com.benefitj.spring.aop.web.EnableAutoAopWebHandler;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableSwaggerApi
@EnableAutoAopWebHandler
@SpringBootApplication
@Slf4j
public class WsChatApp {
  public static void main(String[] args) {
    SpringApplication.run(WsChatApp.class, args);
  }
}
