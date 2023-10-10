package com.benefitj.mybatisplus.quartz;

import com.benefitj.core.EventLoop;
import com.benefitj.mybatisplus.entity.SysQuartzJobTask;
import com.benefitj.mybatisplus.service.QuartzJobTaskService;
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
public class QuartzStartListener {

  @Autowired
  QuartzProperties properties;

  @Autowired
  QuartzJobTaskService service;

  /**
   * 是否启动
   */
  @Value("#{@environment['spring.quartz.task.start-up'] ?: true}")
  private boolean startup;

  public QuartzStartListener() {
  }

  @OnAppStart
  public void onAppStart() {
    if (startup) {
      // 调度任务
      EventLoop.asyncIO(this::scheduleJobTasks, properties.getStartupDelay().getSeconds() * 1000L + 3000L, TimeUnit.MILLISECONDS);
    }
  }

  public void scheduleJobTasks() {
    SysQuartzJobTask condition = new SysQuartzJobTask();
    condition.setActive(Boolean.TRUE);
    List<SysQuartzJobTask> all = service.getList(condition, null, null);
    for (SysQuartzJobTask task : all) {
      QuartzUtils.scheduleJob(service.getScheduler(), task);
    }
  }

}

