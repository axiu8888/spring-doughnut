package com.benefitj.wschat.ws;

import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.websocket.*;
import com.benefitj.wschat.message.ChatMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.atomic.AtomicReference;


@Slf4j
@Component
@WebSocketEndpoint(value = {"/socket/chat"}, socketFactory = ChatSocketEndpoint.ChatWebSocketFactory.class)
public class ChatSocketEndpoint implements WebSocketListener<ChatSocketEndpoint.ChatWebSocket> {

  public static final AtomicReference<WebSocketManager> MANAGER_HOLDER = new AtomicReference<>();

  public static WebSocketManager getManager() {
    return MANAGER_HOLDER.get();
  }


//  final ILoadingCache<String, ChatUser> userCache = ILoadingCache.newWriteCache(Duration.ofMinutes(1), (loader, key) -> {
//    ChatUser user = new ChatUser();
//    user.setId(key);
//    return user;
//  });


  @Override
  public void onWebSocketManager(WebSocketManager manager) {
    if (MANAGER_HOLDER.compareAndSet(null, manager)) {
      log.info("onWebSocketManager: {}", manager.getClass());
    }
  }

  @Override
  public void onOpen(ChatWebSocket socket) {
    log.info("[" + socket.getClass().getSimpleName() + "] 上线, id: {}, 数量: {}", socket.getId(), getManager().size());

    // 发送欢迎消息
    socket.send(JsonUtils.toJson(new ChatMessage(
        ChatMessage.MessageType.JOIN,
        "欢迎加入聊天室",
        "系统",
        "public"
    )));
  }

  @Override
  public void onTextMessage(ChatWebSocket socket, TextMessage message) {
    log.info("[" + socket.getClass().getSimpleName() + "] 消息(Text), id: {}, 数量: {}, msg: {}"
        , socket.getId(), getManager().size(), message.getPayload());

    //socket.send("接收到消息[" + message.getPayload() + "]");

    ChatMessage chatMessage = JsonUtils.fromJson(message.getPayload(), ChatMessage.class);
    // 广播消息给所有连接的用户
    broadcastMessage(socket, chatMessage);
  }

  @Override
  public void onBinaryMessage(ChatWebSocket socket, BinaryMessage message) {
    log.info("[" + socket.getClass().getSimpleName() + "] 消息(Binary), id: {}, 数量3: {}, msg: {}",
        socket.getId(), getManager().size(), HexUtils.toHexString(message.getPayload().array()));
  }

  @Override
  public void onError(ChatWebSocket socket, Throwable error) {
    log.info("[" + socket.getClass().getSimpleName() + "] 抛出异常, id: {}, 数量: {}, cause: {}",
        socket.getId(), getManager().size(), error.getMessage());
  }

  @Override
  public void onClose(ChatWebSocket socket, CloseStatus reason) {
    log.info("[" + socket.getClass().getSimpleName() + "] 下线, id: {}, 数量: {}", socket.getId(), getManager().size());

    // 发送离开消息
    broadcastMessage(socket, new ChatMessage(
        ChatMessage.MessageType.LEAVE,
        "离开了聊天室",
        "用户",
        "public"
    ));
  }


  private void broadcastMessage(WebSocket self, ChatMessage message) {
    String jsonMessage = JsonUtils.toJson(message);
    getManager().forEach((id, ws) -> {
      //if (StringUtils.equalsIgnoreCase(self.getId(), ws.getId())) return;
      if (ws.isOpen()) {
        ws.send(jsonMessage);
      }
    });
  }


  @Component
  public static class ChatWebSocketFactory implements WebSocketFactory<ChatWebSocket> {
    @Override
    public ChatWebSocket create(WebSocketSession session) {
      return new ChatWebSocket(session);
    }
  }


  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class ChatWebSocket extends WebSocketImpl {

    private String username;

    public ChatWebSocket(WebSocketSession session) {
      super(session);
    }

  }

}
