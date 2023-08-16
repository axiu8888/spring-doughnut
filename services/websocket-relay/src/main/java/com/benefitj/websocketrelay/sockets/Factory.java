package com.benefitj.websocketrelay.sockets;

import com.benefitj.spring.websocket.WebSocket;
import com.benefitj.spring.websocket.WebSocketFactory;
import org.springframework.web.socket.WebSocketSession;

public class Factory implements WebSocketFactory {

  @Override
  public WebSocket create(WebSocketSession session) {
    return null;
  }

}
