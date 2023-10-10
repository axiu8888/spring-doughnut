package com.benefitj.spring.quartz;

import com.benefitj.core.ReflectUtils;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.quartz.caller.DefaultJobTaskCaller;
import com.benefitj.spring.quartz.worker.QuartzJobWorker;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * job 任务
 */
public interface JobTaskCaller extends Job {

  @Override
  void execute(JobExecutionContext context) throws JobExecutionException;

  default Class<? extends JobWorker> classForName(String name) throws QuartzException {
    try {
      return (Class<? extends JobWorker>) Class.forName(name);
    } catch (ClassNotFoundException e) {
      throw new QuartzException(e.getMessage());
    }
  }

  /**
   * 创建 JobWorker 实例
   *
   * @param worker JobWorker类
   * @param args   构造函数的可选参数
   * @return 返回实例对象
   */
  default Object newJobWorkerInstance(Class<?> worker, Object... args) {
    return ReflectUtils.newInstance(worker, args);
  }

  /**
   * 创建调用
   *
   * @return 返回
   */
  default QuartzJobWorker newQuartzJobWorker() {
    return new QuartzJobWorker();
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
    return new DefaultJobTaskCaller();
  }

  /**
   * 创建可持久化的调度
   */
  static JobTaskCaller newPersistentJobTaskCaller() {
    return new DefaultJobTaskCaller();
  }

  /**
   * 创建不允许并发的调度
   */
  static JobTaskCaller newDisallowConcurrentJobTaskCaller() {
    return new DefaultJobTaskCaller();
  }

  /**
   * 创建可持久化切不允许并发的调度
   */
  static JobTaskCaller newPersistentWithDisallowConcurrentJobTaskCaller() {
    return new DefaultJobTaskCaller();
  }

}
