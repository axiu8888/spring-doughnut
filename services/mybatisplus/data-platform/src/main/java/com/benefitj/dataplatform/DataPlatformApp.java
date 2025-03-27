package com.benefitj.dataplatform;

import com.benefitj.spring.listener.EnableAppStateListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;


@EnableAppStateListener
@PropertySource(value = {"classpath:version.properties"}, encoding = "utf-8")
@SpringBootApplication
@Slf4j
public class DataPlatformApp {
  public static void main(String[] args) {
    SpringApplication.run(DataPlatformApp.class, args);
  }
}
