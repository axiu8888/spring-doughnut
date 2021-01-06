package com.benefitj.spring.aop;

import java.lang.annotation.*;

/**
 * AOP切入点
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface AopPointCut {
}