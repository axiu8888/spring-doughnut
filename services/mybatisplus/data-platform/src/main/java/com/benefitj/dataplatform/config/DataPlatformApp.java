package com.benefitj.dataplatform.config;

import com.benefitj.spring.listener.AppStateHook;
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

  static {
    AppStateHook.registerStart(evt -> appStart());
    AppStateHook.registerStop(evt -> appStop());
  }

  public static void appStart() {
    try {

    } catch (Exception e) {
      log.error("throws ==>: " + e.getStackTrace(), e);
    }
  }

  public static void appStop() {
  }


}
