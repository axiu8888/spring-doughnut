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
   * 是否为新客户端，不是的话就用系统默认的客户端
   */
  boolean isNewClient() default false;

  /**
   * 自定义的服务端地址
   */
  String serverURI() default "";

  /**
   * 是否异步执行
   */
  boolean async() default false;

  /**
   * 延迟订阅事件
   */
  int startupDelay() default 0;

}
