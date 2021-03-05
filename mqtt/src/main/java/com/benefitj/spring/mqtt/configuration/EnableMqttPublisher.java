package com.benefitj.spring.mqtt.configuration;


import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * MQTT发布
 */
@Lazy
@Import({
    CommonsMqttConfiguration.class,
    MqttPublisherConfiguration.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableMqttPublisher {
}
