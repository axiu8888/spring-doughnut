package com.benefitj.spring.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerEndpoint;
import java.util.*;

/**
 * 注册被 {@link SpringServerEndpoint} 注解的WebSocket组件
 */
@ConditionalOnMissingBean(SpringServerEndpointConfiguration.class)
@EnableWebSocket
@Configuration
public class SpringServerEndpointConfiguration extends DelegatingWebSocketConfiguration implements WebSocketConfigurer {

  @Autowired(required = false)
  private List<SpringWebSocketServer> webSocketServers;
  @Autowired
  private ApplicationContext context;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    final List<SpringWebSocketServer> servers = this.webSocketServers;
    if (!CollectionUtils.isEmpty(servers)) {
      for (SpringWebSocketServer server : servers) {
        Class<?> serverClass = server.getClass();
        // 检查是否被注册多次
        if (serverClass.isAnnotationPresent(ServerEndpoint.class)
            && serverClass.isAnnotationPresent(SpringServerEndpoint.class)) {
          throw new IllegalStateException("[" + serverClass + "]无法注册多次，" +
              "请在\"@ServerEndpoint\"和\"@SpringServerEndpoint\"中删除一个注解!");
        }
      }

      final Map<String, Object> serverEndpoints = context.getBeansWithAnnotation(ServerEndpoint.class);
      final Map<String, Object> webSocketMap = new HashMap<>(serverEndpoints.size());
      serverEndpoints.forEach((s, o) ->
          webSocketMap.put(o.getClass().getAnnotation(ServerEndpoint.class).value(), o));
      for (SpringWebSocketServer server : servers) {
        Class<? extends SpringWebSocketServer> serverClass = server.getClass();
        SpringServerEndpoint endpoint = serverClass.getAnnotation(SpringServerEndpoint.class);
        if (endpoint != null) {
          String match = Arrays.stream(endpoint.value())
              .filter(webSocketMap::containsKey)
              .findFirst()
              .orElse(null);
          if (match != null) {
            throw new IllegalStateException("WebSocket URI [" + match + "]已经被"
                + webSocketMap.get(match).getClass() + "定义了, 重复定义的实现类: " + serverClass);
          }

          // 注册handler
          WebSocketHandlerRegistration registration = registry.addHandler(server, endpoint.value());
          // 允许的域
          registration.setAllowedOrigins(endpoint.allowedOrigins());
          // 拦截器
          Class<? extends HandshakeInterceptor>[] interceptorClasses = endpoint.handshakeInterceptors();
          for (Class<? extends HandshakeInterceptor> klass : interceptorClasses) {
            registration.addInterceptors(context.getBean(klass));
          }
        }
      }
    }
  }

  @ConditionalOnMissingBean(ServerEndpointExporter.class)
  @Bean
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }

}
