package com.benefitj.spring.aop;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Spring组件的AOP处理配置
 */
@EnableAspectJAutoProxy
@Import({ComponentAspect.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableAutoAopComponentHandler {
}
