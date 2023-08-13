package com.benefitj.athenapdfservice;

import com.benefitj.core.cmd.CmdCall;
import com.benefitj.core.cmd.CmdExecutor;
import com.benefitj.spring.aop.log.EnableHttpLoggingHandler;
import com.benefitj.spring.athenapdf.EnableAthenapdf;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.swagger.EnableSwaggerApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:/swagger-api-info.properties", encoding = "utf-8")
@EnableSwaggerApi
@EnableHttpLoggingHandler
@EnableAthenapdf
@SpringBootApplication
@Slf4j
public class AthenapdfApplication {
  public static void main(String[] args) {
    SpringApplication.run(AthenapdfApplication.class, args);
  }

  static {
    AppStateHook.registerStart(event -> checkDockerVersion());
  }

  static void checkDockerVersion() {
    try {
      CmdCall call = CmdExecutor.get().call("docker -v");
      //Docker version 20.10.10, build b485636
      if (StringUtils.isNotBlank(call.getMessage())) {
        log.info(call.getMessage());
      } else {
        log.info("不支持【docker -v】命令\nmessage: {}\nerror: {}", call.getMessage(), call.getError());
      }
    } catch (Exception e) {
      log.error("不支持【docker -v】命令, error: " + e.getMessage(), e);
    }
  }
}
