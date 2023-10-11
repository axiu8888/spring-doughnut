package com.benefitj.spring.quartz;

import com.benefitj.spring.ctx.SpringCtxHolder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * JobWorker
 */
public interface JobWorker {

  String KEY_ID = "id";
  String KEY_JOB = "job";
  String KEY_JOB_CLASS = "jobClass";
  String KEY_JOB_DATA = "jobData";

  /**
   * 执行方法
   *
   * @param context   上下文
   * @param jobDetail detail
   * @param job      任务
   */
  void execute(JobExecutionContext context, JobDetail jobDetail, QuartzJob job) throws JobExecutionException;

  /**
   * 获取Spring的Bean实例
   *
   * @param requiredType 类型
   * @return 返回查询到的实例
   */
  default <T> T getBean(Class<? extends T> requiredType) {
    return SpringCtxHolder.getBean(requiredType);
  }

  /**
   * 获取Spring的Bean实例
   *
   * @param beanName bean名称
   * @return 返回查询到的实例
   */
  default <T> T getBean(String beanName) {
    return SpringCtxHolder.getBean(beanName);
  }
}
