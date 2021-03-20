package com.benefitj.spring.quartzservice;

import java.lang.annotation.*;

/**
 * quartz调度的服务
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface QuartzService {

  /**
   * 名称，如果为空，根据类和方法明产生
   */
  String name() default "";

  /**
   * CRON表达式
   */
  String cron() default "";

  /**
   * 对此功能的描述
   */
  String remarks() default "";

}
