package com.benefit.websocket;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Spring websocket
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SpringServerEndpointConfiguration.class)
@ConditionalOnMissingBean(SpringServerEndpointConfiguration.class)
public @interface EnableSpringWebSocket {
}
