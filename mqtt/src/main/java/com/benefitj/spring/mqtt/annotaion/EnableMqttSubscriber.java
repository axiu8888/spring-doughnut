package com.benefitj.spring.mqtt.annotaion;

import com.benefitj.spring.mqtt.config.MqttSubscriberConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * MQTT订阅
 */
@Lazy
@Import({MqttSubscriberConfiguration.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableMqttSubscriber {
}
