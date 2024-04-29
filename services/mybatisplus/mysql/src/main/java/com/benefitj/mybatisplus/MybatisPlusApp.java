package com.benefitj.mybatisplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

//@EnableAdminServer
@PropertySource(value = {"classpath:version.properties"}, encoding = "utf-8")
@SpringBootApplication
public class MybatisPlusApp {
  public static void main(String[] args) {
    SpringApplication.run(MybatisPlusApp.class, args);
  }
}
