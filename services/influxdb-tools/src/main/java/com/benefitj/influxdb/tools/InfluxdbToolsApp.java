package com.benefitj.influxdb.tools;


import com.benefitj.core.IOUtils;
import com.benefitj.core.ShutdownHook;
import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.influxdb.InfluxOptions;
import com.benefitj.spring.influxdb.spring.EnableInfluxDB;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@EnableInfluxDB
@EnableSwaggerApi
@EnableHttpLoggingHandler
@SpringBootApplication
public class InfluxdbToolsApp {
  public static void main(String[] args) {
    SpringApplication.run(InfluxdbToolsApp.class, args);
  }

  static {
    AppStateHook.registerStart(evt -> {
      InfluxOptions opts = SpringCtxHolder.getBean(InfluxOptions.class);
      boolean autoDelete = opts.getApi().isAutoDelete();
      if (autoDelete) {
        ShutdownHook.register(() -> IOUtils.deleteFile(new File(opts.getApi().getCacheDir())));
      }
    });
  }
}
