package com.benefitj.provider.mqttaclredis;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * MQTT ACL redis配置
 */
@Inherited
@Import(MqttAclRedisConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableMqttAclRedis {
}
