package com.benefitj.websocket;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Spring websocket
 */
@Import(SpringServerEndpointConfiguration.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSpringWebSocket {
}
