package com.benefitj.spring.listener;

import java.lang.annotation.*;

/**
 * APP启动后 {@link org.springframework.context.event.ContextClosedEvent}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface OnAppStart {
}
