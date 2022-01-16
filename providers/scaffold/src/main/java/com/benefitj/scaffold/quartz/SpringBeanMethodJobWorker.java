package com.benefitj.scaffold.quartz;

import com.benefitj.spring.quartz.QuartzJobTask;

import java.lang.reflect.Method;

/**
 * bean方法调用
 */
public class SpringBeanMethodJobWorker {

  /**
   * 对象
   */
  private Object target;
  /**
   * 方法
   */
  private Method method;
  /**
   * 调度服务
   */
  private QuartzJobTask jobTask;



}
