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

import java.util.concurrent.atomic.AtomicReference;

@Component
@WebSocketEndpoint({"/sockets/simple"})
@Slf4j
public class SimpleWebSocket implements WebSocketListener {

  public static final AtomicReference<WebSocketManager> MANAGER_HOLDER = new AtomicReference<>();

  public static WebSocketManager getManager() {
    return MANAGER_HOLDER.get();
  }

  @Override
  public void onWebSocketManager(WebSocketManager manager) {
    if (MANAGER_HOLDER.compareAndSet(null, manager)) {
      log.info("onWebSocketManager: {}", manager.getClass());
    }
  }

  @Override
  public void onOpen(WebSocket socket) {
    log.info("WebSocket上线, id: {}, 数量: {}", socket.getId(), getManager().size());
  }

  @Override
  public void onTextMessage(WebSocket socket, TextMessage message) {
    log.info("WebSocket消息(Text), id: {}, 数量: {}, msg: {}",
        socket.getId(), getManager().size(), message.getPayload());

    socket.send("接收到消息[" + message.getPayload() + "]");
  }

  @Override
  public void onBinaryMessage(WebSocket socket, BinaryMessage message) {
    log.info("WebSocket消息(Binary), id: {}, 数量3: {}, msg: {}",
        socket.getId(), getManager().size(), HexUtils.toHexString(message.getPayload().array()));
  }

  @Override
  public void onError(WebSocket socket, Throwable error) {
    log.info("WebSocket抛出异常, id: {}, 数量: {}, cause: {}",
        socket.getId(), getManager().size(), error.getMessage());
  }

  @Override
  public void onClose(WebSocket socket, CloseStatus reason) {
    log.info("WebSocket下线, id: {}, 数量: {}", socket.getId(), getManager().size());
  }
}
