package com.benefitj.spring.redis;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * redis消息通道监听
 *
 * @author dingxiaun
 */
@Lazy
@Import(RedisMessageListenerConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableRedisMessageListener {
}
