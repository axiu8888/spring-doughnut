package com.benefitj.spring.mqtt;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * MQTT服务配置
 */
@Lazy
@Import(MqttConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableMqttConfiguration {
}
