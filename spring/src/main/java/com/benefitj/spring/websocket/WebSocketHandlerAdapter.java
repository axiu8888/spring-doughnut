package com.benefitj.spring.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

/**
 * WbeSocket
 */
public class WebSocketHandlerAdapter implements WebSocketHandler {

  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * WebSocket 管理器
   */
  private WebSocketManager socketManager;
  /**
   * WebSocket 工厂
   */
  private WebSocketFactory socketFactory;
  /**
   * WebSocket 监听
   */
  private WebSocketListener socketListener;
  /**
   * 注解
   */
  private WebSocketEndpoint endpoint;

  public WebSocketHandlerAdapter() {
  }

  public WebSocketHandlerAdapter(WebSocketFactory socketFactory) {
    this.socketFactory = socketFactory;
  }

  /**
   * WebSocket Open
   *
   * @param session Session
   */
  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    WebSocket socket = getSocketFactory().create(session);
    getSocketManager().put(socket.getId(), socket);
    getSocketListener().onOpen(socket);
  }

  /**
   * 接收到消息
   *
   * @param session Session
   * @param message 消息
   */
  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    WebSocket socket = getSocketManager().get(session.getId());
    if (message instanceof TextMessage) {
      getSocketListener().onTextMessage(socket, (TextMessage) message);
    } else if (message instanceof BinaryMessage) {
      getSocketListener().onBinaryMessage(socket, (BinaryMessage) message);
    } else if (message instanceof PingMessage) {
      getSocketListener().onPingMessage(socket, (PingMessage) message);
    } else {
      log.warn("unknown message: {}, {}, payloadLength: {}"
          , message.getClass()
          , message.getPayload().getClass()
          , message.getPayloadLength());
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    WebSocket socket = getSocketManager().get(session.getId());
    getSocketListener().onError(socket, exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    WebSocket socket = getSocketManager().remove(session.getId());
    getSocketListener().onClose(socket, closeStatus);
  }

  @Override
  public boolean supportsPartialMessages() {
    return getSocketListener().supportsPartialMessages();
  }

  public WebSocketManager getSocketManager() {
    return socketManager;
  }

  public void setSocketManager(WebSocketManager socketManager) {
    this.socketManager = socketManager;
  }

  public WebSocketFactory getSocketFactory() {
    return socketFactory;
  }

  public void setSocketFactory(WebSocketFactory socketFactory) {
    this.socketFactory = socketFactory;
  }

  public WebSocketListener getSocketListener() {
    return socketListener;
  }

  public void setSocketListener(WebSocketListener socketListener) {
    this.socketListener = socketListener;
  }

  public WebSocketEndpoint getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(WebSocketEndpoint endpoint) {
    this.endpoint = endpoint;
  }
}
