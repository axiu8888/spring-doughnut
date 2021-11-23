package com.benefitj.spring.websocket;

import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket客户端实现
 */
public class WebSocketImpl implements WebSocket {

  private WebSocketSession session;

  public WebSocketImpl() {
  }

  public WebSocketImpl(WebSocketSession session) {
    this.session = session;
  }

  public void setSession(WebSocketSession session) {
    this.session = session;
  }

  @Override
  public WebSocketSession getSession() {
    return session;
  }

}
