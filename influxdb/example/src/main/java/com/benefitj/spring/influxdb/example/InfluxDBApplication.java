package com.benefitj.spring.influxdb.example;

import com.benefitj.spring.influxdb.spring.EnableInfluxWriterManager;
import com.benefitj.spring.influxdb.spring.EnableRxJavaInfluxDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableInfluxWriterManager
@EnableRxJavaInfluxDB
@SpringBootApplication
public class InfluxDBApplication {
  public static void main(String[] args) {
    SpringApplication.run(InfluxDBApplication.class, args);
  }
}
