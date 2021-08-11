package com.benefitj.spring.websocket;

import org.springframework.web.socket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * WebSocket服务端
 */
public abstract class SpringWebSocketServerEndpoint implements SpringWebSocket {

  private static final Map<Class<? extends SpringWebSocketServerEndpoint>, Map<String, SpringWebSocketClient>> SOCKETS = new ConcurrentHashMap<>();

  public static Map<String, SpringWebSocketClient> getSocketMap(Class<? extends SpringWebSocketServerEndpoint> type) {
    return SOCKETS.get(type);
  }

  public static Map<String, SpringWebSocketClient> removeSocketMap(Class<? extends SpringWebSocketServerEndpoint> type) {
    return SOCKETS.remove(type);
  }

  private static final Function<Class<? extends SpringWebSocketServerEndpoint>, Map<String, SpringWebSocketClient>>
      SOCKETS_CREATOR = type -> new ConcurrentHashMap<>(10);


  @Override
  public final void afterConnectionEstablished(WebSocketSession session) throws Exception {
    Map<String, SpringWebSocketClient> socketMap = SOCKETS.computeIfAbsent(getClass(), SOCKETS_CREATOR);
    SpringWebSocketClient client = socketMap.computeIfAbsent(session.getId(), s -> createClient(session));
    client.onOpen();
  }

  @Override
  public final void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
    if (message instanceof TextMessage) {
      TextMessage msg = ((TextMessage) message);
      useClient(session, c -> c.onTextMessage(msg.getPayload(), msg.isLast()));
    } else if (message instanceof BinaryMessage) {
      BinaryMessage msg = (BinaryMessage) message;
      useClient(session, c -> c.onBinaryMessage(msg.getPayload(), msg.isLast()));
    } else if (message instanceof PingMessage) {
      useClient(session, c -> c.onPingMessage((PingMessage) message));
    }
  }

  @Override
  public final void handleTransportError(WebSocketSession session, Throwable exception) {
    useClient(session, c -> c.onError(exception));
  }

  @Override
  public final void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
    useClient(session, c -> c.onClose(closeStatus.getReason(), closeStatus.getCode()));
    removeClient(session);
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

  /**
   * 创建 WebSocket客户端
   *
   * @param session 会话
   * @return 返回客户端
   */
  public abstract SpringWebSocketClient createClient(WebSocketSession session);

  /**
   * 获取客户端
   *
   * @param session 会话
   * @return 返回客户端
   */
  protected SpringWebSocketClient getClient(WebSocketSession session) {
    return getSocketMap(getClass()).get(session.getId());
  }

  /**
   * 移除客户端
   *
   * @param session 会话
   * @return 返回被移除的客户端
   */
  protected SpringWebSocketClient removeClient(WebSocketSession session) {
    return getSocketMap(getClass()).remove(session.getId());
  }

  protected SpringWebSocketClient useClient(WebSocketSession session, Consumer<SpringWebSocketClient> c) {
    SpringWebSocketClient client = getClient(session);
    if (client != null) {
      c.accept(client);
    }
    return client;
  }

}
