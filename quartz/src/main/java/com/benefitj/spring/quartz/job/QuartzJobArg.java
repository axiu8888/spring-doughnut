package com.benefitj.spring.quartz.job;


import java.lang.annotation.*;

/**
 * 调度任务参数
 *
 * @author dxa
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface QuartzJobArg {
  /**
   * 名称
   */
  String name() default "";

  /**
   * 描述
   */
  String description();

  /**
   * 参数类型
   */
  ArgType type();

}
