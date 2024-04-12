package com.benefitj.spring.websocket;


import com.benefitj.spring.ctx.EnableSpringCtxInit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Collection;

/**
 * WebSocket 配置
 */
@ConditionalOnWebApplication
@ConditionalOnClass(EnableWebSocket.class)
@EnableSpringCtxInit
@EnableWebSocket
@Configuration
public class AutoWebSocketConfiguration extends DelegatingWebSocketConfiguration
    implements WebSocketConfigurer, BeanFactoryAware {

  private ConfigurableListableBeanFactory beanFactory;

  @ConditionalOnMissingBean(name = "websocketFactory")
  @Bean("websocketFactory")
  public WebSocketFactory websocketFactory() {
    return WebSocketFactory.INSTANCE;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    if (beanFactory instanceof ConfigurableListableBeanFactory) {
      this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    ConfigurableListableBeanFactory factory = this.beanFactory;
    final Collection<WebSocketListener> listeners;
    try {
      listeners = factory.getBeansOfType(WebSocketListener.class).values();
    } catch (BeansException e) {
      return;
    }

    for (WebSocketListener socketListener : listeners) {
      Class<? extends WebSocketListener> type = socketListener.getClass();
      if (!type.isAnnotationPresent(WebSocketEndpoint.class)) {
        throw new IllegalStateException("\"" + type.getName() + "\"需要被@WebSocketEndpoint注解注释!");
      }

      WebSocketEndpoint endpoint = type.getAnnotation(WebSocketEndpoint.class);
      WebSocketHandlerAdapter adapter = new WebSocketHandlerAdapter();
      if (endpoint.socketManager() == WebSocketManager.class) {
        adapter.setSocketManager(WebSocketManager.newInstance());
      } else {
        adapter.setSocketManager(beanFactory.getBean(endpoint.socketManager()));
      }
      adapter.setSocketFactory(beanFactory.getBean(endpoint.socketFactory()));
      adapter.setSocketListener(socketListener);
      // 初始化 SocketManager
      socketListener.onWebSocketManager(adapter.getSocketManager());
      // 注册handler
      WebSocketHandlerRegistration registration = registry.addHandler(adapter, endpoint.value());
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
