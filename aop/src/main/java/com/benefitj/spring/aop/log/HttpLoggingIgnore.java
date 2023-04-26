package com.benefitj.spring.aop.log;

import java.lang.annotation.*;

/**
 * 忽略日志打印
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface HttpLoggingIgnore {
}