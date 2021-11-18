package com.benefitj.spring.vertxmqtt.subscriber;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * MQTT消息订阅
 */
@Import({MqttSubscriberConfiguration.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface EnableMqttSubscriber {
}
