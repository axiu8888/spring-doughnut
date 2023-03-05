package com.benefitj.spring.mvc.query;

import java.lang.annotation.*;

/**
 * 查询请求
 *
 * @author DINGXIUAN
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Inherited
public @interface QueryBody {

  /**
   * 前缀
   */
  String value() default "";

}
