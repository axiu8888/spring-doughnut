package com.benefitj.spring.websocket;

import org.springframework.web.socket.*;

import java.io.IOException;

public interface WebSocket {

  /**
   * Session
   */
  WebSocketSession getSession();

  /**
   * 获取 Session ID
   */
  default String getId() {
    return getSession().getId();
  }

  /**
   * 发送文本数据
   *
   * @param text 数据
   * @return 返回发送的结果
   */
  default void send(String text) {
    send(new TextMessage(text));
  }

  /**
   * 发送字节数据
   *
   * @param data 数据
   * @return 返回发送的结果
   */
  default void send(byte[] data) {
    send(new BinaryMessage(data));
  }

  /**
   * 发送字节数据
   *
   * @param data 数据
   * @return 返回发送的结果
   */
  default void send(WebSocketMessage<?> data) {
    try {
      getSession().sendMessage(data);
    } catch (IOException e) {
      throw new WebSocketException(e);
    }
  }

  /**
   * 当前的WebSocket是否处于打开状态
   */
  default boolean isOpen() {
    return getSession().isOpen();
  }

  /**
   * 关闭当前会话
   */
  default void close() {
    try {
      getSession().close();
    } catch (IOException ignore) { /* ~ */ }
  }

}
