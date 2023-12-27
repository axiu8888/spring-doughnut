package com.benefitj.natproxy.udptcp;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用UDP-TCP代理
 */
@Documented
@Import(UdpTcpConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EnableUdpTcpProxy {
}
