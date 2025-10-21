package com.benefitj.wschat.ws;

import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.websocket.WebSocket;
import com.benefitj.spring.websocket.WebSocketEndpoint;
import com.benefitj.spring.websocket.WebSocketListener;
import com.benefitj.spring.websocket.WebSocketManager;
import com.benefitj.wschat.message.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;

import java.util.concurrent.atomic.AtomicReference;


@Component
@WebSocketEndpoint({"/socket/chat"})
@Slf4j
public class ChatSocket implements WebSocketListener {

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

    // 发送欢迎消息
    socket.send(JsonUtils.toJson(new ChatMessage(
        ChatMessage.MessageType.JOIN,
        "欢迎加入聊天室",
        "系统",
        "public"
    )));
  }

  @Override
  public void onTextMessage(WebSocket socket, TextMessage message) {
    log.info("WebSocket消息(Text), id: {}, 数量: {}, msg: {}",
        socket.getId(), getManager().size(), message.getPayload());

    //socket.send("接收到消息[" + message.getPayload() + "]");

    ChatMessage chatMessage = JsonUtils.fromJson(message.getPayload(), ChatMessage.class);
    // 广播消息给所有连接的用户
    broadcastMessage(chatMessage);
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

    // 发送离开消息
    broadcastMessage(new ChatMessage(
        ChatMessage.MessageType.LEAVE,
        "离开了聊天室",
        "用户",
        "public"
    ));
  }


  private void broadcastMessage(ChatMessage message) {
    String jsonMessage = JsonUtils.toJson(message);
    getManager().forEach((s, ws) -> {
      if (ws.isOpen()) {
        ws.send(jsonMessage);
      }
    });
  }
}
