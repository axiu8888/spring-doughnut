package com.benefitj.samples.config;


import com.benefitj.samples.websocket.SpringUserWebSocketClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketConfiguration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置
 */
@Configuration
public class SpringWebSocketConfig extends DelegatingWebSocketConfiguration implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new SpringUserWebSocketClient(), "/websocket/users");
  }

}
