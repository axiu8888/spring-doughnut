package com.benefitj.spring.listener;

import java.lang.annotation.*;

/**
 * APP停止时, {@link org.springframework.context.event.ContextClosedEvent}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface OnAppStop {
}
