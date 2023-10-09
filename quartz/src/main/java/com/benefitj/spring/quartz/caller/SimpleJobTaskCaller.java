package com.benefitj.spring.quartz.caller;

import com.benefitj.spring.quartz.JobTaskCaller;
import com.benefitj.spring.quartz.JobWorker;
import com.benefitj.spring.quartz.WorkerType;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调用 job task
 */
public class SimpleJobTaskCaller implements JobTaskCaller {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  public SimpleJobTaskCaller() {
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {
      JobDetail detail = context.getJobDetail();
      JobDataMap jobDataMap = detail.getJobDataMap();
      String taskId = jobDataMap.getString(JobWorker.KEY_ID);
      String worker = jobDataMap.getString(JobWorker.KEY_WORKER);

      WorkerType workerType = WorkerType.of(jobDataMap.getString(JobWorker.KEY_WORKER_TYPE));
      Object jobWorker = null;
      switch (workerType) {
        case NEW_INSTANCE:
          jobWorker = newJobWorkerInstance(classForName(worker));
          break;
        case SPRING_BEAN_NAME:
          jobWorker = getBean(worker);
          break;
        case SPRING_BEAN_CLASS:
          jobWorker = getBean(classForName(worker));
          break;
        case QUARTZ_JOB_WORKER:
          jobWorker = newQuartzJobWorker();
          break;
      }
      if (jobWorker != null) {
        if (jobWorker instanceof JobWorker) {
          ((JobWorker) jobWorker).execute(context, detail, taskId);
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
