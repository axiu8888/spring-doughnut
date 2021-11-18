package com.benefitj.spring.mqtt.subscriber;

import java.lang.annotation.*;

/**
 * MQTT消息订阅
 *
 * @author DINGXIUAN
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MqttMessageListener {

  /**
   * 订阅主题
   */
  String[] topics();

  /**
   * 客户端ID的前缀
   */
  String clientIdPrefix() default "mqtt-";

  /**
   * 是否为单个客户端
   */
  boolean singleClient() default false;


}
