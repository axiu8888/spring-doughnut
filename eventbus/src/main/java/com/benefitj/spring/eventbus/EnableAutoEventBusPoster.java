package com.benefitj.spring.eventbus;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * EventBus注册
 */
@Import({EventBusConfiguration.class})
@Lazy
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAutoEventBusPoster {
}
