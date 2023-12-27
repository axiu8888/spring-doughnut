package com.benefitj.natproxy.udp;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用UDP代理
 */
@Documented
@Import(UdpConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EnableUdpProxy {
}
