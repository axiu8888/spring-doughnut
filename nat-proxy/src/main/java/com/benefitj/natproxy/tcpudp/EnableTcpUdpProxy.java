package com.benefitj.natproxy.tcpudp;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用TCP-UDP代理
 */
@Documented
@Import(TcpUdpConfiguration.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EnableTcpUdpProxy {
}
