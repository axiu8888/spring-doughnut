package com.benefitj.spring.quartz;

import com.benefitj.core.DateFmtter;
import com.benefitj.spring.ctx.SpringCtxHolder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * JobWorker
 */
public interface JobWorker {

  String KEY_ID = "id";
  String KEY_JOB_DATA = "jobData";
  String KEY_WORKER = "worker";
  String KEY_WORKER_TYPE = "workerType";

  /**
   * 执行方法
   *
   * @param context   上下文
   * @param jobDetail detail
   * @param taskId    任务ID
   */
  void execute(JobExecutionContext context, JobDetail jobDetail, String taskId) throws JobExecutionException;

  /**
   * 格式化时间 pattern: yyyy-MM-dd HH:mm:ss.SSS
   *
   * @param date 时间 Date|Long
   * @return 返回格式化好的时间
   */
  default String fmtS(Object date) {
    return date != null ? DateFmtter.fmtS(date) : null;
  }

  /**
   * 格式化时间 pattern: yyyy-MM-dd HH:mm:ss
   *
   * @param date 时间 Date|Long
   * @return 返回格式化好的时间
   */
  default String fmt(Object date) {
    return date != null ? DateFmtter.fmt(date) : null;
  }

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
