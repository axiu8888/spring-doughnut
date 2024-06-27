package com.benefitj.spring.redis;

import java.lang.annotation.*;

/**
 * 消息通道
 *
 * @author DINGXIUAN
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RedisMessageListener {
  /**
   * example   channel:test
   *
   * @return 消息通道
   */
  String[] value();

  /**
   * 是否异步执行
   */
  boolean async() default false;

}