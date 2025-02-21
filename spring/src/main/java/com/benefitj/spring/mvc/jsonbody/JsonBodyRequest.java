package com.benefitj.spring.mvc.jsonbody;


import java.lang.annotation.*;

/**
 * Json请求体
 *
 * @author DINGXIUAN
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface JsonBodyRequest {

  /**
   * 处理的 JsonBodyProcessor
   */
  Class<? extends JsonBodyProcessor> value() default JsonBodyProcessor.class;

  /**
   * 来源
   */
  From[] source() default {From.spring, From.create};


  enum From {
    spring,
    create,
    ;
  }

}
