package com.benefitj.spring.redis;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * redis分布式锁
 *
 * @author dingxiaun
 */
@Lazy
@Import(RedisLockConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableRedisLock {
}
