package com.benefitj.spring.mqtt.annotaion;


import com.benefitj.spring.mqtt.config.MqttPublisherConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * MQTT发布
 */
@Lazy
@Import({MqttPublisherConfiguration.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableMqttPublisher {
}
