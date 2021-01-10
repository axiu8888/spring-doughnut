package com.benefitj.spring.dynamicdatasource.aop;

import java.lang.annotation.*;

/**
 * 动态数据源代理
 *
 * @author DINGXIUAN
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSourceHandler {

  /**
   * 读取
   */
  String[] slave() default {};

  /**
   * 写入
   */
  String[] master() default {};

  /**
   * 支持的策略
   */
  Strategy strategy() default Strategy.AUTO;


  enum Strategy {
    /**
     * 默认，根据实现者自定义
     */
    AUTO,
    /**
     * 单一，只支持注解中的定义
     */
    SINGLE,
    /**
     * 组合，默认和注解中定义的都支持，默认优先注解中定义的
     */
    COMPOSITE;
  }

}
