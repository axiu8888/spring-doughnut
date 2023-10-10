package com.benefitj.spring.quartz.caller;

import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.quartz.JobTaskCaller;
import com.benefitj.spring.quartz.JobWorker;
import com.benefitj.spring.quartz.QuartzJobTask;
import com.benefitj.spring.quartz.QuartzJobTaskImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 调用 job task
 */
@Slf4j
public class DefaultJobTaskCaller implements JobTaskCaller {

  public DefaultJobTaskCaller() {
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {
      JobDetail detail = context.getJobDetail();
      JobDataMap jobDataMap = detail.getJobDataMap();
      String taskJson = jobDataMap.getString(JobWorker.KEY_TASK);
      QuartzJobTask task = JsonUtils.fromJson(taskJson, QuartzJobTaskImpl.class);

      String worker = task.getWorker();
      Object jobWorker = null;
      switch (task.getWorkerType()) {
        case NEW_INSTANCE:
          jobWorker = newJobWorkerInstance(classForName(worker));
          break;
        case SPRING_BEAN_NAME:
          jobWorker = getBean(worker);
          break;
        case QUARTZ_WORKER:
          jobWorker = newQuartzJobWorker();
          break;
      }
      if (jobWorker != null) {
        if (jobWorker instanceof JobWorker) {
          ((JobWorker) jobWorker).execute(context, detail, task);
        } else {
          log.warn("Fail JobWorker instance: {}", jobWorker.getClass());
        }
      } else {
        log.warn("Not found JobWorker instance: {}", worker);
      }
    } catch (Exception e) {
      log.error("throws: " + e.getMessage(), e);
      throw new JobExecutionException(e);
    }
  }


}
