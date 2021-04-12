package com.benefitj.spring.mvc.request;


import java.lang.annotation.*;

/**
 * get请求
 *
 * @author DINGXIUAN
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Inherited
public @interface GetBody {

  /**
   * 请求参数类型
   */
  Class<?> type() default Object.class;

}
