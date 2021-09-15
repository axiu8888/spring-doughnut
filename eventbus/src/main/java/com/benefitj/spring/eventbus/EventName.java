package com.benefitj.spring.eventbus;

import java.lang.annotation.*;

/**
 * 事件的名称
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface EventName {

  /**
   * 名称
   */
  String value();

}
