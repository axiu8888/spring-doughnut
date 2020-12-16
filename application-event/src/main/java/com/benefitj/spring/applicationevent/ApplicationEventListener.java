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
   * 适配器工厂名称
   */
  String adapterFactoryName() default "";

  /**
   * 适配器工厂类型
   */
  Class<? extends EventAdapterFactory> adapterFactory() default DefaultEventAdapterFactory.class;

}
