package com.benefitj.spring.websocket;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * WebSocket客戶端
 */
public abstract class SpringWebSocketClient implements WebSocketClient<WebSocketSession> {

  private final WebSocketSession session;

  public SpringWebSocketClient(WebSocketSession session) {
    this.session = session;
    // 2MB
    session.setTextMessageSizeLimit((1024 << 10) * 2);
  }

  @Override
  public WebSocketSession getSession() {
    return session;
  }

  /**
   * 获取 Session ID
   */
  @Override
  public String getId() {
    return getSession().getId();
  }

  /**
   * 发送文本数据
   *
   * @param text 数据
   */
  @Override
  public void sendText(String text) {
    try {
      getSession().sendMessage(new TextMessage(text));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 发送字节数据
   *
   * @param data 数据
   */
  @Override
  public void sendBinary(byte[] data) {
    sendBinary(ByteBuffer.wrap(data));
  }

  /**
   * 发送字节数据
   *
   * @param data 数据
   */
  @Override
  public void sendBinary(ByteBuffer data) {
    try {
      getSession().sendMessage(new BinaryMessage(data));
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 当前的WebSocket是否处于打开状态
   */
  @Override
  public boolean isOpen() {
    return getSession().isOpen();
  }

  /**
   * 关闭当前会话
   */
  @Override
  public void close() {
    try {
      getSession().close();
    } catch (IOException ignore) {
    }
  }

  /**
   * ping
   *
   * @param message
   */
  public void onPingMessage(PingMessage message) {
    // ~
  }

}
