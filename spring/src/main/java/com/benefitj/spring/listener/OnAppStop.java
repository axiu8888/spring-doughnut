package com.benefitj.spring.listener;

import java.lang.annotation.*;

/**
 * APP停止时, {@link org.springframework.boot.context.event.ApplicationReadyEvent}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface OnAppStop {
}
