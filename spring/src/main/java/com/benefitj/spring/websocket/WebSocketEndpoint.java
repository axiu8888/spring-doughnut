package com.benefitj.spring.websocket;


import org.springframework.web.socket.server.HandshakeInterceptor;

import java.lang.annotation.*;


/**
 * WebSocket 端点
 *
 * @author Administrator
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface WebSocketEndpoint {
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

  /**
   * WebSocketManager
   */
  Class<? extends WebSocketManager> socketManager() default WebSocketManager.class;

  /**
   * WebSocketFactory
   */
  Class<? extends WebSocketFactory> socketFactory() default WebSocketFactory.class;

}
