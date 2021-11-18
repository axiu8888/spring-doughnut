package com.benefitj.spring.vertxmqtt.server;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用的MQTT服务端
 *
 * @author dingxiuan
 */
@Import({MqttServerConfiguration.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EnableMqttServer {
}
