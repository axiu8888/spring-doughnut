package com.benefitj.spring.quartz.worker;


import java.lang.annotation.*;

/**
 * 调度任务
 *
 * @author dxa
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface QuartzWorker {
  /**
   * 名称
   */
  String name();

  /**
   * 描述
   */
  String description();

}
