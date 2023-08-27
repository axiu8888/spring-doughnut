package com.benefitj.spring.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket客户端实现
 */
public class WebSocketImpl implements WebSocket {

  private WebSocketSession session;

  private final Map<String, Object> attrs = new ConcurrentHashMap<>();

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

  @Override
  public Map<String, Object> attrs() {
    return attrs;
  }

}
