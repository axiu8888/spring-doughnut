package com.benefitj.spring.mqtt.subscriber;

import java.lang.annotation.*;

/**
 * MQTT消息订阅，可选择的参数：<br/>
 * 1. topic(String) 主题 <br/>
 * 2. payload(byte[]) 有效载荷 <br/>
 * 3. {@link org.springframework.messaging.Message<byte[]>} <br/>
 * 4. {@link org.eclipse.paho.client.mqttv3.MqttMessage} <br/>
 * 5. {@link org.springframework.messaging.MessageHeaders} <br/>
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
  String clientIdPrefix() default "";

  /**
   * 是否为单个客户端
   */
  boolean singleClient() default false;


}
