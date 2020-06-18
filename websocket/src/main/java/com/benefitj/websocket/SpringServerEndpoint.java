package com.benefitj.websocket;

import org.springframework.web.socket.server.HandshakeInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Spring WebSocket注解
 *
 * @author DINGXIUAN
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpringServerEndpoint {

  /**
   * URI or URI-template that the annotated class should be mapped to.
   *
   * @return The URI or URI-template that the annotated class should be mapped
   * to.
   */
  String[] value();

  /**
   * 允许的域
   */
  String[] allowedOrigins() default "*";

  /**
   * 拦截器
   */
  Class<? extends HandshakeInterceptor>[] handshakeInterceptors() default {};

}
