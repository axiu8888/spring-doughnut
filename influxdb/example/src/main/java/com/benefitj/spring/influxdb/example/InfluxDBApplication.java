package com.benefitj.spring.influxdb.example;

import com.benefitj.spring.influxdb.enable.EnableAutoRxJavaInfluxDBConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoRxJavaInfluxDBConfiguration
@SpringBootApplication
public class InfluxDBApplication {
  public static void main(String[] args) {
    SpringApplication.run(InfluxDBApplication.class, args);
  }
}
