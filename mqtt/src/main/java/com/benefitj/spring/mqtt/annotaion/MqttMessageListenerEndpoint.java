package com.benefitj.spring.mqtt.annotaion;

import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface MqttMessageListenerEndpoint {

  /**
   * 主题
   */
  String[] topics();

  /**
   * 服务质量
   */
  int qos() default 0;

  /**
   * 默认创建心客户端
   */
  ClientMode clientMode() default @ClientMode;

  /**
   * 消息转换器的类型
   */
  Class<? extends MqttMessageConverter> converter() default DefaultPahoMessageConverter.class;

  /**
   * 客户端模式
   */
  @interface ClientMode {
    /**
     * 是否使用共享的客户端
     */
    boolean share() default true;

    /**
     * 客户端ID，近在创建新的客户端时有效
     */
    String clientId() default "";
  }
}
