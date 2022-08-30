package com.benefitj.spring.mqtt.event;

import java.lang.annotation.*;

/**
 * 参数
 * 
 * @author dingxiuan
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EventTopic {

  /**
   * 匹配规则：/event/#{deviceId}/cmd/#{type}
   */
  String[] value();

  /**
   * 事件转换器
   */
  Class<?> converter() default JsonEventConverter.class;

}
