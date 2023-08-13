package com.benefitj.websocketrelay.sockets;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.spring.websocket.WebSocket;
import com.benefitj.spring.websocket.WebSocketEndpoint;
import com.benefitj.spring.websocket.WebSocketListener;
import com.benefitj.spring.websocket.WebSocketManager;
import com.benefitj.websocketrelay.payload.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;

import java.util.concurrent.atomic.AtomicReference;

@Component
@WebSocketEndpoint({"/sockets/consumer"})
@Slf4j
public class ConsumerWebSocket implements WebSocketListener {

  static final AtomicReference<WebSocketManager> managerRef = new AtomicReference<>();

  public static WebSocketManager getManager() {
    return managerRef.get();
  }

  public static void send(String clientId, Message msg) {

  }


  final JSONObject platform = new JSONObject();

  @Override
  public void onWebSocketManager(WebSocketManager manager) {
    if (managerRef.get() == null) {
      managerRef.set(manager);
      log.debug("[consumer] onWebSocketManager: {}", manager.getClass());
    }
  }

  @Override
  public void onOpen(WebSocket socket) {
    log.debug("[consumer] WebSocket上线, id: {}, 数量: {}", socket.getId(), getManager().size());
  }

  @Override
  public void onTextMessage(WebSocket socket, TextMessage message) {
    log.debug("[consumer] WebSocket消息(Text), id: {}, 数量: {}, msg: {}",
        socket.getId(), getManager().size(), message.getPayload());
  }

  @Override
  public void onBinaryMessage(WebSocket socket, BinaryMessage message) {
    log.debug("[consumer] WebSocket消息(Binary), id: {}, 数量3: {}, msg: {}",
        socket.getId(), getManager().size(), HexUtils.toHexString(message.getPayload().array()));
  }

  @Override
  public void onError(WebSocket socket, Throwable error) {
    log.debug("[consumer] WebSocket抛出异常, id: {}, 数量: {}, cause: {}",
        socket.getId(), getManager().size(), error.getMessage());
  }

  @Override
  public void onClose(WebSocket socket, CloseStatus reason) {
    log.debug("[consumer] WebSocket下线, id: {}, 数量: {}", socket.getId(), getManager().size());
  }
}
