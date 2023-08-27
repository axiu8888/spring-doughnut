package com.benefitj.websocketrelay.sockets;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.spring.websocket.WebSocket;
import com.benefitj.spring.websocket.WebSocketEndpoint;
import com.benefitj.spring.websocket.WebSocketListener;
import com.benefitj.spring.websocket.WebSocketManager;
import com.benefitj.websocketrelay.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;

import java.util.concurrent.atomic.AtomicReference;

@Component
@WebSocketEndpoint("/sockets/producer")
@Slf4j
public class ProducerWebSocket implements WebSocketListener {

  static final AtomicReference<WebSocketManager> managerRef = new AtomicReference<>();

  public static WebSocketManager getManager() {
    return managerRef.get();
  }

  @Autowired
  ProducerMessageDispatcher dispatcher;

  @Override
  public void onWebSocketManager(WebSocketManager manager) {
    if (managerRef.get() == null) {
      managerRef.set(manager);
      log.debug("[producer] onWebSocketManager: {}", manager.getClass());
    }
  }

  @Override
  public void onOpen(WebSocket socket) {
    log.debug("[producer] WebSocket上线, id: {}, 数量: {}", socket.getId(), getManager().size());
  }

  @Override
  public void onTextMessage(WebSocket socket, TextMessage message) {
    log.debug("[producer] WebSocket消息(Text), id: {}, 数量: {}, msg: {}",
        socket.getId(), getManager().size(), message.getPayload());
    JSONObject json = JSON.parseObject(message.getPayload());
    Message msg = json.toJavaObject(Message.class);
    msg.setJson(json);
    dispatcher.dispatch(socket, msg);
  }

  @Override
  public void onBinaryMessage(WebSocket socket, BinaryMessage message) {
    log.debug("[producer] WebSocket消息(Binary), id: {}, 数量3: {}, msg: {}",
        socket.getId(), getManager().size(), HexUtils.toHexString(message.getPayload().array()));
  }

  @Override
  public void onError(WebSocket socket, Throwable error) {
    log.debug("[producer] WebSocket抛出异常, id: {}, 数量: {}, cause: {}",
        socket.getId(), getManager().size(), error.getMessage());
  }

  @Override
  public void onClose(WebSocket socket, CloseStatus reason) {
    log.debug("[producer] WebSocket下线, id: {}, reason: {}, 数量: {}", socket.getId(), reason, getManager().size());
  }
}
