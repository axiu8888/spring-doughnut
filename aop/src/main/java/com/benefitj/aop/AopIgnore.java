package com.benefitj.aop;

import java.lang.annotation.*;

/**
 * 忽略
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface AopIgnore {
}