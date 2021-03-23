package com.benefitj.spring.websocket;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * WebSocket服务端
 */
public abstract class JavaxWebSocketServerEndpoint extends JavaxWebSocket {

  private static final Map<Class<? extends JavaxWebSocketServerEndpoint>, Map<String, JavaxWebSocketClient>> SOCKETS = new ConcurrentHashMap<>();

  public static Map<String, JavaxWebSocketClient> getSocketMap(Class<? extends JavaxWebSocketServerEndpoint> type) {
    return SOCKETS.get(type);
  }

  public static Map<String, JavaxWebSocketClient> removeSocketMap(Class<? extends JavaxWebSocketServerEndpoint> type) {
    return SOCKETS.remove(type);
  }

  private static final Function<Class<? extends JavaxWebSocket>, Map<String, JavaxWebSocketClient>>
      SOCKETS_CREATOR = type -> new ConcurrentHashMap<>(10);

  /**
   * WebSocket Open
   *
   * @param session Session
   */
  @Override
  public final void onOpen(Session session) {
    Map<String, JavaxWebSocketClient> socketMap = SOCKETS.computeIfAbsent(getClass(), SOCKETS_CREATOR);
    JavaxWebSocketClient client = socketMap.computeIfAbsent(session.getId(), s -> createClient(session));
    client.onOpen();
  }

  /**
   * 接收到消息
   *
   * @param session Session
   * @param text    文本数据
   * @param isLast  是否为最后的数据
   */
  @Override
  public final void onTextMessage(Session session, String text, boolean isLast) {
    getClient(session).onTextMessage(text, isLast);
  }

  /**
   * 接收到二进制数据
   *
   * @param session Session
   * @param buffer  数据
   * @param isLast  是否为最后的数据
   */
  @Override
  public final void onBinaryMessage(Session session, ByteBuffer buffer, boolean isLast) {
    getClient(session).onBinaryMessage(buffer, isLast);
  }

  /**
   * 发生错误时调用
   *
   * @param session Session
   * @param e       异常
   */
  @Override
  public final void onError(Session session, Throwable e) {
    getClient(session).onError(e);
  }

  /**
   * 连接关闭调用的方法
   *
   * @param session Session
   * @param reason
   */
  @Override
  public final void onClose(Session session, CloseReason reason) {
    CloseReason.CloseCode closeCode = reason.getCloseCode();
    removeClient(session).onClose(reason.getReasonPhrase(), closeCode != null ? closeCode.getCode() : 0);
  }

  /**
   * 创建 WebSocket客户端
   *
   * @param session 会话
   * @return 返回客户端
   */
  public abstract JavaxWebSocketClient createClient(Session session);

  /**
   * 获取客户端
   *
   * @param session 会话
   * @return 返回客户端
   */
  protected JavaxWebSocketClient getClient(Session session) {
    return getSocketMap(getClass()).get(session.getId());
  }

  /**
   * 移除客户端
   *
   * @param session 会话
   * @return 返回被移除的客户端
   */
  protected JavaxWebSocketClient removeClient(Session session) {
    return getSocketMap(getClass()).remove(session.getId());
  }

}
