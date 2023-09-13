package com.benefitj.mybatisplus;

import com.benefitj.core.EventLoop;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.quartz.EnableQuartz;
import com.benefitj.spring.quartz.job.QuartzJobManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.util.Set;

@Slf4j
@EnableQuartz
@PropertySource(value = {"classpath:version.properties"}, encoding = "utf-8")
@SpringBootApplication
public class MybatisPlusApp {
  public static void main(String[] args) {
    SpringApplication.run(MybatisPlusApp.class, args);
  }

  @OnAppStart
  public void onAppStart() {
//    EventLoop.asyncIO(() -> {
//      SysUserEntity user = SysUserEntity.builder()
//          .orgId("123456")
//          .name("张三 \uD83D\uDE04")
//          .birthday(TimeUtils.toDate(1984, 5, 22))
//          .gender(GenderEnum.MALE)
//          .locked(Boolean.FALSE)
//          .build();
//      SpringCtxHolder.getBean(SysUserService.class).insert(user);
//    }, 3, TimeUnit.SECONDS);

    EventLoop.asyncIO(() -> {

      QuartzJobManager manager = SpringCtxHolder.getBean(QuartzJobManager.class);
      Set<String> names = manager.keySet();
      log.info("quartz names: {}", names);

      // 调用
      manager.get(names.stream().findFirst().orElse("")).invoke();

    }, 1000);

  }

}
