package com.benefitj.spring.quartz.job;


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
public @interface QuartzJob {
  /**
   * 名称
   */
  String name();

  /**
   * 描述
   */
  String description();

}
