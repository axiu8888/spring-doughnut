package com.benefitj.spring.quartz;

import com.benefitj.core.ReflectUtils;
import com.benefitj.spring.ctx.SpringCtxHolder;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface JobTaskCaller extends Job {

  @Override
  void execute(JobExecutionContext context) throws JobExecutionException;

  default Class<? extends JobWorker> classForName(String name) throws SchedulerException {
    try {
      return (Class<? extends JobWorker>) Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new SchedulerException(e.getMessage());
    }
  }

  /**
   * 创建 JobWorker 实例
   *
   * @param worker JobWorker类
   * @return 返回实例对象
   */
  default Object newJobWorkerInstance(Class<?> worker) {
    return ReflectUtils.newInstance(worker);
  }

  /**
   * 获取bean实例
   *
   * @param requiredType bean类型
   * @param <T>          类型
   * @return 返回实例
   */
  default <T> T getBean(Class<T> requiredType) {
    return SpringCtxHolder.getBean(requiredType);
  }

  /**
   * 获取bean实例
   *
   * @param name bean名称
   * @param <T>  类型
   * @return 返回实例
   */
  default <T> T getBean(String name) {
    return SpringCtxHolder.getBean(name);
  }

  /**
   * 创建默认的调度
   */
  static JobTaskCaller newJobTaskCaller() {
    return new SimpleJobTaskCaller();
  }

  /**
   * 创建可持久化的调度
   */
  static JobTaskCaller newPersistentJobTaskCaller() {
    return new SimpleJobTaskCaller();
  }

  /**
   * 创建不允许并发的调度
   */
  static JobTaskCaller newDisallowConcurrentJobTaskCaller() {
    return new SimpleJobTaskCaller();
  }

  /**
   * 创建可持久化切不允许并发的调度
   */
  static JobTaskCaller newPersistentWithDisallowConcurrentJobTaskCaller() {
    return new SimpleJobTaskCaller();
  }

  /**
   * 调用 job task
   */
  class SimpleJobTaskCaller implements JobTaskCaller {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

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
        }
        if (jobWorker != null) {
          if (jobWorker instanceof JobWorker) {
            ((JobWorker) jobWorker).execute(context, detail, taskId);
          } else {
            logger.warn("Fail JobWorker instance: " + jobWorker.getClass());
          }
        } else {
          logger.warn("Not found JobWorker instance: " + worker);
        }
      } catch (Exception e) {
        logger.error("throws: " + e.getMessage(), e);
        throw new JobExecutionException(e);
      }
    }


  }

  /**
   * 执行后持久化数据
   */
  @PersistJobDataAfterExecution
  class PersistentJobTaskCaller extends SimpleJobTaskCaller {
  }

  /**
   * 不允许并发执行
   */
  @DisallowConcurrentExecution
  class DisallowConcurrentJobTaskCaller extends SimpleJobTaskCaller {
  }

  /**
   * 执行后持久化数据，并且不允许并发执行
   */
  @PersistJobDataAfterExecution
  @DisallowConcurrentExecution
  class PersistentWithDisallowConcurrentJobTaskCaller extends SimpleJobTaskCaller {
  }

}
