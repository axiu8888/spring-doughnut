package com.benefitj.spring.eventbus;

import java.lang.annotation.*;

/**
 * 忽略订阅
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SubscriberIgnore {
}
