package com.benefitj.spring.mqtt.config;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * MQTT订阅
 */
@Lazy
@Import({
    CommonsMqttConfiguration.class,
    MqttSubscriberConfiguration.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableMqttSubscriber {
}
