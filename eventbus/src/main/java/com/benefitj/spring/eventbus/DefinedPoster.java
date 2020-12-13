package com.benefitj.spring.eventbus;


import com.benefitj.event.EventBusPoster;

import java.lang.annotation.*;

/**
 * 指定监听的EventBusPoster
 *
 * @author DINGXIUAN
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DefinedPoster {

  /**
   * EventBusPoster的bean名称
   */
  String name() default "";

  /**
   * EventBusPoster的class类型
   */
  Class<? extends EventBusPoster> type() default EventBusPoster.class;

}
