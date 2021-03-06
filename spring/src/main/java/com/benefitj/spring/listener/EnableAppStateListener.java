package com.benefitj.spring.listener;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import({AutoAppStateConfiguration.class})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EnableAppStateListener {
}
