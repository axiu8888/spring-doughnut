package com.benefitj.influxdb.tools;


import com.benefitj.spring.aop.web.EnableAutoAopWebHandler;
import com.benefitj.spring.influxdb.spring.EnableInflux;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableInflux
@EnableSwaggerApi
@EnableAutoAopWebHandler
@SpringBootApplication
public class InfluxdbToolsApp {
  public static void main(String[] args) {
    SpringApplication.run(InfluxdbToolsApp.class, args);
  }
}
