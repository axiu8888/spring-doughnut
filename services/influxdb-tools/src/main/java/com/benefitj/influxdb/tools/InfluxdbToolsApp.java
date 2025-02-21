package com.benefitj.influxdb.tools;


import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.ShutdownHook;
import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.influxdb.InfluxOptions;
import com.benefitj.spring.influxdb.spring.EnableInfluxdb;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@EnableInfluxdb
@EnableSwaggerApi
@EnableHttpLoggingHandler
@SpringBootApplication
@Slf4j
public class InfluxdbToolsApp {
  public static void main(String[] args) {
    SpringApplication.run(InfluxdbToolsApp.class, args);
  }

  static {
    AppStateHook.registerStart(evt -> {
      InfluxOptions opts = SpringCtxHolder.getBean(InfluxOptions.class);
      boolean autoDelete = opts.getApi().isAutoDelete();
      if (autoDelete) {
        ShutdownHook.register(() -> IOUtils.delete(new File(opts.getApi().getCacheDir())));
      }

      InfluxTemplate template = SpringCtxHolder.getBean(InfluxTemplate.class);
      String uploadDir = SpringCtxHolder.getEnvProperty("upload-dir", "./lines");
      File linesDir = new File(uploadDir);
      log.info("linesDir -->: {}, exist: {}", linesDir.getAbsolutePath(), linesDir.exists());
      List<File> lines = IOUtils.listFiles(linesDir, f -> true, true);
      log.info("lines -->: \n{}", lines.stream().map(File::getAbsolutePath).collect(Collectors.joining("\n")));
      lines
          .stream()
          .filter(File::isFile)
          .filter(f -> f.getName().endsWith(".line"))
          .peek(f -> log.info("上传 -> {}, {}MB", f.getAbsolutePath(), f.length() * 1.0 / IOUtils.MB))
          .forEach(f -> {
            template.write(f);
            IOUtils.delete(f);
          });
      EventLoop.asyncIO(() -> System.exit(0), 1000);
    });
  }
}
