package com.benefitj.examples.websocket;

import com.benefitj.spring.websocket.WebSocket;
import com.benefitj.spring.websocket.WebSocketEndpoint;
import com.benefitj.spring.websocket.WebSocketListener;
import com.benefitj.spring.websocket.WebSocketManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;

@Slf4j
@Component
@WebSocketEndpoint({"/sockets/simple"})
public class SimpleWebSocket implements WebSocketListener {

  private WebSocketManager manager;

  @Override
  public void onWebSocketManager(WebSocketManager manager) {
    if (this.manager == null) {
      this.manager = manager;
      log.info("onWebSocketManager: {}", manager.getClass());
    }
  }

  @Override
  public void onOpen(WebSocket socket) {
    log.info("WebSocket上线, id: {}, 数量: {}", socket.getId(), manager.size());
  }

  @Override
  public void onTextMessage(WebSocket socket, TextMessage message) {
    log.info("WebSocket消息(Text), id: {}, 数量: {}, msg: {}",
        socket.getId(), manager.size(), message.getPayload());

    socket.send("接收到消息[" + message.getPayload() + "]");
  }

  @Override
  public void onBinaryMessage(WebSocket socket, BinaryMessage message) {
    log.info("WebSocket消息(Binary), id: {}, 数量3: {}, msg: {}",
        socket.getId(), manager.size(), HexUtils.toHexString(message.getPayload().array()));
  }

  @Override
  public void onError(WebSocket socket, Throwable error) {
    log.info("WebSocket抛出异常, id: {}, 数量: {}, cause: {}",
        socket.getId(), manager.size(), error.getMessage());
  }

  @Override
  public void onClose(WebSocket socket, CloseStatus reason) {
    log.info("WebSocket下线, id: {}, 数量: {}", socket.getId(), manager.size());
  }
}
