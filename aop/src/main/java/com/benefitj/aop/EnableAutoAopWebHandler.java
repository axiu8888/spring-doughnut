package com.benefitj.aop;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Aop Web 请求自动配置
 */
@EnableAspectJAutoProxy
@Import({AopWebRequestAspect.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableAutoAopWebHandler {
}
