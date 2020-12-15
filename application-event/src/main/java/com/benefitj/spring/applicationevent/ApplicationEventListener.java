package com.benefitj.spring.applicationevent;

import java.lang.annotation.*;

/**
 * 监听注解
 *
 * @author DINGXIUAN
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ApplicationEventListener {

  /**
   * 适配器类型
   */
  Class<? extends ApplicationEventAdapter> adapterType() default ApplicationEventAdapter.class;

}
