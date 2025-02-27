package com.benefitj.provider.mqttaclredis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * MQTT ACL redis配置
 */
@Import(MqttAclRedisConfiguration.class)
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableMqttAclRedis {
}
