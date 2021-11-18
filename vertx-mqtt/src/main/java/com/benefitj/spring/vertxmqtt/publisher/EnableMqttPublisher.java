package com.benefitj.spring.vertxmqtt.publisher;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * MQTT消息发布，
 */
@Import({MqttPublisherConfiguration.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface EnableMqttPublisher {
}
