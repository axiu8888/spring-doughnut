package com.benefitj.spring.websocket;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;

/**
 * WebSocket 监听
 */
public interface WebSocketListener {

  /**
   * WebSocket Open
   *
   * @param socket WebSocket
   */
  void onOpen(WebSocket socket);

  /**
   * 接收到消息
   *
   * @param socket  WebSocket
   * @param message 文本消息
   */
  void onTextMessage(WebSocket socket, TextMessage message);

  /**
   * 接收到二进制消息
   *
   * @param socket  WebSocket
   * @param message 二进制消息
   */
  void onBinaryMessage(WebSocket socket, BinaryMessage message);

  /**
   * 接收到Ping消息
   *
   * @param socket  WebSocket
   * @param message Ping消息
   */
  default void onPingMessage(WebSocket socket, PingMessage message) {
    // ~
  }

  /**
   * 发生错误时调用
   *
   * @param socket WebSocket
   * @param error  异常
   */
  void onError(WebSocket socket, Throwable error);

  /**
   * 连接关闭调用的方法
   *
   * @param socket WebSocket
   */
  void onClose(WebSocket socket, CloseStatus reason);

  /**
   * 是否支持分片传输
   */
  default boolean supportsPartialMessages() {
    return false;
  }

}
