package com.benefitj.mybatisplus.quartz;

import com.benefitj.mybatisplus.service.SysJobService;
import com.benefitj.spring.quartz.JobWorker;
import com.benefitj.spring.quartz.QuartzJobListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 删除
 */
@Component
@Slf4j
public class DeleteJobListener implements QuartzJobListener {

  private SysJobService service;

  public SysJobService getService() {
    return service;
  }

  @Lazy
  @Autowired
  public void setService(SysJobService service) {
    this.service = service;
  }

  @Override
  public String getName() {
    return "deleteJobListener";
  }

  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException jee) {
    try {
      // job执行完毕
      final Date nextFireTime = context.getNextFireTime();
      final JobDetail jd = context.getJobDetail();
      if (nextFireTime == null && jd != null) {
        JobDataMap jdm = jd.getJobDataMap();
        String id = jdm.getString(JobWorker.KEY_ID);
        if (StringUtils.isNotBlank(id)) {
          // 删除Job
          getService().delete(id);
        }
        log.warn("调度任务执行完毕, 删除调度任务, key[{}]  id[{}] result: {},  job throws: {}",
            jd.getKey(), id, context.getResult(), (jee != null ? jee.getMessage() : null));
      }
    } catch (Exception e) {
      log.warn("throw: {}", e.getMessage());
    }
  }

}
