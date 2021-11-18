package com.benefitj.spring.mqtt.publisher;


import com.benefitj.spring.mqtt.CommonsMqttConfiguration;
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
