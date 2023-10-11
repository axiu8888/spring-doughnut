package com.benefitj.mybatisplus.quartz;

import com.benefitj.core.EventLoop;
import com.benefitj.mybatisplus.entity.SysJob;
import com.benefitj.mybatisplus.service.SysJobService;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.quartz.QuartzUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 启动调度程序
 */
@Component
@Slf4j
public class AppJobStarter {

  @Autowired
  QuartzProperties properties;

  @Autowired
  SysJobService service;

  /**
   * 是否启动
   */
  @Value("#{@environment['spring.quartz.job.auto-load'] ?: true}")
  private boolean autoLoad;

  public AppJobStarter() {
  }

  @OnAppStart
  public void onAppStart() {
    if (autoLoad) {
      // 调度任务
      long delay = properties.getStartupDelay().getSeconds() * 1000L + 3000L;
      EventLoop.asyncIO(this::startJobs, delay, TimeUnit.MILLISECONDS);
    }
  }

  public void startJobs() {
    SysJob condition = new SysJob();
    condition.setActive(Boolean.TRUE);
    List<SysJob> list = service.getList(condition, null, null);
    for (SysJob job : list) {
      QuartzUtils.scheduleJob(service.getScheduler(), job);
    }
  }

}

