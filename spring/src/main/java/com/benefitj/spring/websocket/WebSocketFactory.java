package com.benefitj.spring.websocket;

import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket 工厂
 */
public interface WebSocketFactory<WS extends WebSocket> {

  WebSocketFactory<WebSocket> INSTANCE = new WebSocketFactoryImpl();

  /**
   * 创建 WebSocket 客户端
   *
   * @param session 会话
   * @return 返回新的客户端
   */
  WebSocket create(WebSocketSession session);

  class WebSocketFactoryImpl implements WebSocketFactory<WebSocket> {

    @Override
    public WebSocket create(WebSocketSession session) {
      return new WebSocketImpl(session);
    }
  }

}
