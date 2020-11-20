package com.benefitj.spring.influxdb.dto;

import java.lang.annotation.*;

/**
 * tag是否允许为null
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface InfluxTagNullable {

  /**
   * tag是否允许为null
   *
   * @return 返回是否支持
   */
  boolean value() default false;

}
