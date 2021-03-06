package com.benefitj.spring.aop.web;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Aop Web 请求自动配置
 */
@EnableAspectJAutoProxy
@Import({WebRequestAdvice.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableAutoAopWebHandler {
}
