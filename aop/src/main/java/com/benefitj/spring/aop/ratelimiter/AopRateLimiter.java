package com.benefitj.spring.aop.ratelimiter;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface AopRateLimiter {

  int NOT_LIMITED = 0;

  /**
   * qps
   */
  @AliasFor("qps") int value() default NOT_LIMITED;

  /**
   * qps
   */
  @AliasFor("value") int qps() default NOT_LIMITED;

  /**
   * 超时时长
   */
  int timeout() default 60;

  /**
   * 超时时间单位
   */
  TimeUnit timeoutUnit() default TimeUnit.SECONDS;

}
