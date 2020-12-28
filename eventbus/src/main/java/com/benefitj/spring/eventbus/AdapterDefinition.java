package com.benefitj.spring.eventbus;

import java.lang.annotation.*;

/**
 * 指定监听的 EventBusAdapter
 *
 * @author DINGXIUAN
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AdapterDefinition {

  /**
   * 指定工厂对象
   */
  Class<? extends EventBusAdapterFactory> value() default DefaultEventBusAdapterFactory.class;

}
