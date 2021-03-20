package com.benefitj.examples.quartz;

import com.benefitj.spring.quartzservice.QuartzService;
import org.springframework.stereotype.Component;

@Component
public class QuartzExecutorService {

  @QuartzService(
      name = "test",
      cron = "0 0/1 ? * * *",
      remarks = "测试quartz"
  )
  public void execute() {
    System.err.println("测试quartz.....");
  }

}
