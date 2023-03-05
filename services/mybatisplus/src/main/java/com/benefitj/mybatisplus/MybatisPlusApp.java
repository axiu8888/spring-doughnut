package com.benefitj.mybatisplus;

import com.benefitj.spring.listener.OnAppStart;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource(value = {"classpath:version.properties"}, encoding = "utf-8")
@SpringBootApplication
public class MybatisPlusApp {
  public static void main(String[] args) {
    SpringApplication.run(MybatisPlusApp.class, args);
  }

  @OnAppStart
  public void onAppStart() {
//    EventLoop.io().schedule(() -> {
//      SysUserEntity user = SysUserEntity.builder()
//          .orgId("123456")
//          .name("张三")
//          .birthday(TimeUtils.toDate(1984, 5, 22))
//          .gender(GenderEnum.MALE)
//          .locked(Boolean.FALSE)
//          .build();
//      SpringCtxHolder.getBean(SysUserService.class).insert(user);
//    }, 3, TimeUnit.SECONDS);
  }

}
