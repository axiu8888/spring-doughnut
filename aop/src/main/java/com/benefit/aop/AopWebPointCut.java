package com.benefit.aop;

import java.lang.annotation.*;

/**
 * AOP切入点
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface AopWebPointCut {
}