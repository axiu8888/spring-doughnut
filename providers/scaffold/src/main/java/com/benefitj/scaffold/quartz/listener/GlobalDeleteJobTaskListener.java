package com.benefitj.scaffold.quartz.listener;

import com.benefitj.scaffold.quartz.QuartzJobTaskService;
import com.benefitj.spring.quartz.JobWorker;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.Date;

/**
 * 删除
 */
public class GlobalDeleteJobTaskListener implements SimpleJobListener {

  private static final Logger logger = LoggerFactory.getLogger(GlobalDeleteJobTaskListener.class);

  private QuartzJobTaskService service;

  public QuartzJobTaskService getService() {
    return service;
  }

  @Lazy
  @Autowired
  public void setService(QuartzJobTaskService service) {
    this.service = service;
  }

  @Override
  public String getName() {
    return "deleteListener";
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
          // 删除JobTask
          getService().delete(id);
        }
        logger.warn("调度任务执行完毕, 删除调度任务, key[{}]  id[{}] result: {},  job throws: {}",
            jd.getKey(), id, context.getResult(), (jee != null ? jee.getMessage() : null));
      }
    } catch (Exception e) {
      logger.warn("throw: {}", e.getMessage());
    }
  }

}
