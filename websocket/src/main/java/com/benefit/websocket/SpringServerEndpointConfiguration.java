package com.benefit.websocket;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.websocket.server.ServerEndpoint;
import java.util.List;

/**
 * 注册被 {@link SpringServerEndpoint} 注解的WebSocket组件
 */
@Lazy
@EnableWebSocket
@Configuration
public class SpringServerEndpointConfiguration implements WebSocketConfigurer {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired(required = false)
  private List<SpringWebSocketServer> webSocketServers;
  @Autowired
  private ApplicationContext context;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    final List<SpringWebSocketServer> servers = this.webSocketServers;
    if (!CollectionUtils.isEmpty(servers)) {
      for (SpringWebSocketServer server : servers) {
        Class<?> serverClazz = server.getClass();
        // 检查是否被注册多次
        if (serverClazz.isAnnotationPresent(ServerEndpoint.class)
            && serverClazz.isAnnotationPresent(SpringServerEndpoint.class)) {
          throw new IllegalStateException("[" + serverClazz + "]无法注册多次，" +
              "请在\"@ServerEndpoint\"和\"@SpringServerEndpoint\"中删除一个注解!");
        }
      }

      for (SpringWebSocketServer server : servers) {
        Class<? extends SpringWebSocketServer> serverClazz = server.getClass();
        SpringServerEndpoint endpoint = serverClazz.getAnnotation(SpringServerEndpoint.class);
        if (endpoint != null) {
          // 注册handler
          WebSocketHandlerRegistration registration = registry.addHandler(server, endpoint.value());
          // 允许的域
          registration.setAllowedOrigins(endpoint.allowedOrigins());
          // 拦截器
          Class<? extends HandshakeInterceptor>[] interceptorClasses = endpoint.handshakeInterceptors();
          for (Class<? extends HandshakeInterceptor> clazz : interceptorClasses) {
            registration.addInterceptors(context.getBean(clazz));
          }
          logger.info("注册WebSocket服务: {}", JSON.toJSONString(endpoint.value()));
        }
      }
    }
  }

}
