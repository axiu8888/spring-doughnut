package com.benefit.aop;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Aop Web 请求自动配置
 */
@EnableAspectJAutoProxy
@ConditionalOnMissingBean(AopWebRequestAspect.class)
@Import({AopWebRequestAspect.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableAutoAopWebHandler {
}
