package com.benefitj.spring.websocket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerEndpoint;
import java.util.*;

/**
 * 注册被 {@link SpringWebSocketServerEndpoint} 注解的WebSocket组件
 */
@EnableWebSocket
@Configuration
public class SpringServerEndpointConfiguration extends DelegatingWebSocketConfiguration
    implements WebSocketConfigurer, BeanFactoryAware {

  private ConfigurableListableBeanFactory beanFactory;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    if (beanFactory instanceof ConfigurableListableBeanFactory) {
      this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    ConfigurableListableBeanFactory factory = this.beanFactory;
    final Collection<SpringWebSocket> sockets;
    try {
      sockets = factory.getBeansOfType(SpringWebSocket.class).values();
    } catch (BeansException e) {
      e.printStackTrace();
      return;
    }

    if (!CollectionUtils.isEmpty(sockets)) {
      for (SpringWebSocket socket : sockets) {
        Class<?> socketClass = socket.getClass();
        // 检查是否被注册多次
        if (socketClass.isAnnotationPresent(ServerEndpoint.class)
            && socketClass.isAnnotationPresent(SpringWebSocketServerEndpoint.class)) {
          throw new IllegalStateException("[" + socketClass + "]无法注册多次，" +
              "请在\"@ServerEndpoint\"和\"@SpringServerEndpoint\"中删除一个注解!");
        }
      }

      final Map<String, Object> serverEndpoints = factory.getBeansWithAnnotation(ServerEndpoint.class);
      final Map<String, Object> webSocketMap = new HashMap<>(serverEndpoints.size());
      serverEndpoints.forEach((s, o) ->
          webSocketMap.put(o.getClass().getAnnotation(ServerEndpoint.class).value(), o));
      for (SpringWebSocket socket : sockets) {
        Class<? extends SpringWebSocket> socketType = socket.getClass();
        SpringWebSocketServerEndpoint endpoint = socketType.getAnnotation(SpringWebSocketServerEndpoint.class);
        if (endpoint != null) {
          String match = Arrays.stream(endpoint.value())
              .filter(webSocketMap::containsKey)
              .findFirst()
              .orElse(null);
          if (match != null) {
            throw new IllegalStateException("WebSocket URI [" + match + "]已经被"
                + webSocketMap.get(match).getClass() + "定义了, 重复定义的实现类: " + socketType);
          }

          // 注册handler
          WebSocketHandlerRegistration registration = registry.addHandler(socket, endpoint.value());
          // 允许的域
          registration.setAllowedOrigins(endpoint.allowedOrigins());
          // 拦截器
          Class<? extends HandshakeInterceptor>[] interceptors = endpoint.handshakeInterceptors();
          for (Class<? extends HandshakeInterceptor> interceptor : interceptors) {
            registration.addInterceptors(factory.getBean(interceptor));
          }
        }
      }
    }
  }

  @ConditionalOnMissingBean
  @Bean
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }
}
