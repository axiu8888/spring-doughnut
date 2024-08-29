package com.benefitj.spring.aop.log;

import java.lang.annotation.*;

/**
 * 忽略日志打印
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Inherited
@Documented
public @interface HttpLoggingIgnore {
}