package com.benefitj.spring.mvc.multibody;

import java.lang.annotation.*;

/**
 * Controller中方法接收多个JSON对象
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MultiRequestBody {
  /**
   * 是否必须出现的参数
   */
  boolean required() default true;

  /**
   * 当value的值或者参数名不匹配时，是否允许解析最外层属性到该对象
   */
  boolean parseAllFields() default true;

  /**
   * 解析时用到的JSON的key
   */
  String value() default "";

}
