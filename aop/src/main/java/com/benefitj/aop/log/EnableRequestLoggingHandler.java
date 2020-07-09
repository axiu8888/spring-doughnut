package com.benefitj.aop.log;

import com.benefitj.aop.EnableAutoAopWebHandler;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 打印请求日志
 */
@EnableAutoAopWebHandler
@Import({HttpServletRequestLoggingHandler.class})
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EnableRequestLoggingHandler {
}
