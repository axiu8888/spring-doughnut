package com.benefitj.mybatisplus;

import com.benefitj.core.*;
import com.benefitj.mybatisplus.model.GenderEnum;
import com.benefitj.mybatisplus.model.SysUserEntity;
import com.benefitj.mybatisplus.service.SysUserService;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.EnableAppStateListener;
import com.benefitj.spring.listener.OnAppStart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
@PropertySource(value = {"classpath:version.properties"}, encoding = "utf-8")
@EnableSpringCtxInit
@EnableAppStateListener
@SpringBootApplication
public class MybatisPlusApp {
  public static void main(String[] args) {
    SpringApplication.run(MybatisPlusApp.class, args);
  }

  @OnAppStart
  public void onAppStart() {
    EventLoop.io().schedule(() -> {
      SysUserEntity user = SysUserEntity.builder()
          .orgId("123456")
          .name("张三")
          .birthday(TimeUtils.toDate(1984, 5, 22))
          .gender(GenderEnum.MALE)
          .locked(Boolean.FALSE)
          .build();
      SpringCtxHolder.getBean(SysUserService.class).insert(user);
    }, 3, TimeUnit.SECONDS);


    EventLoop.io().schedule(() -> CatchUtils.tryThrow(() -> {
      String ip = InetAddress.getLocalHost().getHostAddress();
      String port = SpringCtxHolder.getServerPort();
      String path = SpringCtxHolder.getServerContextPath();
      String swaggerBaseUrl = SpringCtxHolder.getEnvProperty("springfox.documentation.swagger-ui.base-url");
      swaggerBaseUrl = DUtils.withs(swaggerBaseUrl, "/", "/");
      String address = ip + ":" + port + path;
      log.info("\n---------------------------------------------------------------------------------\n\t" +
          "[ " + SpringCtxHolder.getAppName() + " ] is running! Access URLs:\n\t" +
          "Local: \t\t\thttp://localhost:" + port + path + "/\n\t" +
          "External: \t\thttp://" + address + "/\n\t" +
          "Swagger文档: \thttp://" + address + swaggerBaseUrl + "swagger-ui/index.html\n\t" +
          "knife4j文档: \thttp://" + address + "/doc.html\n" +
          "---------------------------------------------------------------------------------");
    }), 1, TimeUnit.SECONDS);

  }

}
