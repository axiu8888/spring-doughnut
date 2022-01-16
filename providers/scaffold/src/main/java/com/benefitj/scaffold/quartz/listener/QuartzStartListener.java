package com.benefitj.scaffold.quartz.listener;

import com.benefitj.core.EventLoop;
import com.benefitj.core.ReflectUtils;
import com.benefitj.scaffold.quartz.QuartzJobTaskService;
import com.benefitj.scaffold.quartz.entity.SysJobTaskEntity;
import com.benefitj.spring.listener.AppStartListener;
import com.benefitj.spring.quartz.QuartzUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 启动调度程序
 */
public class QuartzStartListener implements AppStartListener {

  private SchedulerFactoryBean schedulerFactoryBean;
  private QuartzJobTaskService service;

  /**
   * 是否启动
   */
  @Value("#{@environment['spring.quartz.task.start-up'] ?: true}")
  private boolean startup;

  public QuartzStartListener() {
  }

  public QuartzStartListener(SchedulerFactoryBean schedulerFactoryBean,
                             QuartzJobTaskService service) {
    this.schedulerFactoryBean = schedulerFactoryBean;
    this.service = service;
  }

  @Override
  public void onAppStart(ApplicationReadyEvent event) {
    if (startup) {
      Class<?> type = getSchedulerFactoryBean().getClass();
      Field field = ReflectUtils.getField(type, "startupDelay");
      if (field != null) {
        Integer value = ReflectUtils.getFieldValue(field, getSchedulerFactoryBean());
        // 调度任务
        EventLoop.io().schedule(() -> scheduleJobTasks(getService())
            , value + 3000
            , TimeUnit.MILLISECONDS);
      } else {
        // 调度任务
        EventLoop.io().schedule(() -> scheduleJobTasks(getService())
            , 5
            , TimeUnit.SECONDS);
      }
    }
  }

  public void scheduleJobTasks(QuartzJobTaskService service) {
    SysJobTaskEntity condition = new SysJobTaskEntity();
    condition.setActive(Boolean.TRUE);
    List<SysJobTaskEntity> all = service.getList(condition, null, null);
    for (SysJobTaskEntity task : all) {
      QuartzUtils.scheduleJob(service.getScheduler(), task);
    }
  }

  public SchedulerFactoryBean getSchedulerFactoryBean() {
    return schedulerFactoryBean;
  }

  public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
    this.schedulerFactoryBean = schedulerFactoryBean;
  }

  public QuartzJobTaskService getService() {
    return service;
  }

  public void setService(QuartzJobTaskService service) {
    this.service = service;
  }
}

