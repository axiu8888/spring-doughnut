package com.benefitj.spring.websocket;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Spring websocket
 */
@Inherited
@Import(SpringServerEndpointConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSpringWebSocket {
}
