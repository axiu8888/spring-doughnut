package com.benefitj.spring.eventbus;

import java.lang.annotation.*;

/**
 * 订阅的名称
 *
 * @author DINGXIUAN
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface SubscriberName {

  /**
   * 名称
   */
  String[] name() default {};

  /**
   * 匹配规则
   */
  String pattern() default "";

  /**
   * 是否强制匹配名称，默认强制
   */
  boolean force() default true;

}
