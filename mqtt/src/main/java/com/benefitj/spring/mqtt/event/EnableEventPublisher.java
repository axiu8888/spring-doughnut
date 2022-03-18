package com.benefitj.spring.mqtt.event;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * MQTT event publisher
 */
@Lazy
@Import({ EventPublisher.class })
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableEventPublisher {
}
