package com.benefitj.natproxy.tcp;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用TCP代理
 */
@Documented
@Import(TcpConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EnableTcpProxy {
}
