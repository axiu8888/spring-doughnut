package com.benefitj.spring.mqtt;

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
public @interface MqttMessageListener {

  /**
   * 订阅主题
   */
  String[] topics();

  /**
   * 服务质量
   */
  int qos() default 0;

  /**
   * 客户端ID的前缀
   */
  String clientIdPrefix() default "mqtt-";

}
