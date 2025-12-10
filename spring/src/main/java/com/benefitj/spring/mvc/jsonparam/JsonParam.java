package com.benefitj.spring.mvc.jsonparam;

import java.lang.annotation.*;


@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JsonParam {

  /**
   * 参数名
   */
  String value() default "";

}
