package com.benefitj.spring.quartz.caller;

import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.quartz.JobCaller;
import com.benefitj.spring.quartz.JobWorker;
import com.benefitj.spring.quartz.QuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 调用 job
 */
@Slf4j
public class DefaultJobCaller implements JobCaller {

  public DefaultJobCaller() {
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {
      JobDetail detail = context.getJobDetail();
      JobDataMap jobDataMap = detail.getJobDataMap();
      String jobJson = jobDataMap.getString(JobWorker.KEY_JOB);
      String jobClass = jobDataMap.getString(JobWorker.KEY_JOB_CLASS);
      QuartzJob job = JsonUtils.fromJson(jobJson, classForName(jobClass));

      String worker = job.getWorker();
      Object jobWorker = null;
      switch (job.getWorkerType()) {
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
          ((JobWorker) jobWorker).execute(context, detail, job);
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
