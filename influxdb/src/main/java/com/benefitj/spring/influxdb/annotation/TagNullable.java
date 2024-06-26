package com.benefitj.spring.influxdb.annotation;

import java.lang.annotation.*;

/**
 * tag是否允许为null
 *
 * @author DINGXIUAN
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface TagNullable {

  /**
   * tag是否允许为null
   *
   * @return 返回是否支持
   */
  boolean value() default true;

}
