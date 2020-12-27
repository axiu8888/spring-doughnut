package com.benefitj.spring.security;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * ~
 */
@Import({HttpSecurityCustomizerConfiguration.class})
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableHttpSecurityCustomizerConfiguration {
}
