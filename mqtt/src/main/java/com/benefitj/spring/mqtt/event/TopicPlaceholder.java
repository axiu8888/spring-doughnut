package com.benefitj.spring.mqtt.event;

import java.lang.annotation.*;

/**
 * 主题占位符标记
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface TopicPlaceholder {
  /**
   * 字段在topic占位符中的名称
   */
  String name();

}
