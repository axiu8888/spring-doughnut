package com.benefitj.spring.vertxmqtt.subscriber;

import java.lang.annotation.*;

/**
 * 订阅
 *
 * @author DINGXIUAN
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MqttSubscriber {

  /**
   * 订阅主题
   */
  String[] value();

}
