package com.benefitj.spring.applicationevent;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * SpringBoot 事件监听
 */
@Import({ApplicationEventListenerAdapter.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAutoApplicationListener {
}
