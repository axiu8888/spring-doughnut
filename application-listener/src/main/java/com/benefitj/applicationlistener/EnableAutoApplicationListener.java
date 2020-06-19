package com.benefitj.applicationlistener;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * SpringBoot 事件监听
 */
@Import({EventListenerAdapter.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAutoApplicationListener {
}
