package com.benefitj.spring.websocket;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@ConditionalOnMissingBean(AutoWebSocketConfiguration.class)
@Import(AutoWebSocketConfiguration.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EnableWebSocketEndpoint {
}
