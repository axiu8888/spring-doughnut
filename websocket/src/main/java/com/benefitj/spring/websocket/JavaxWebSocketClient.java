package com.benefitj.spring.websocket;


import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * WebSocket客户端
 */
public abstract class JavaxWebSocketClient implements WebSocketClient<Session> {

  private final Session session;

  public JavaxWebSocketClient(Session session) {
    this.session = session;
  }

  @Override
  public Session getSession() {
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
   * @return 返回发送的结果
   */
  @Override
  public void sendText(String text) {
    try {
      getSession().getBasicRemote().sendText(text);
    } catch (IOException e) {
      throw new WebSocketException(e);
    }
  }

  /**
   * 发送字节数据
   *
   * @param data 数据
   * @return 返回发送的结果
   */
  @Override
  public void sendBinary(byte[] data) {
    sendBinary(ByteBuffer.wrap(data));
  }

  /**
   * 发送字节数据
   *
   * @param data 数据
   * @return 返回发送的结果
   */
  @Override
  public void sendBinary(ByteBuffer data) {
    try {
      getSession().getBasicRemote().sendBinary(data);
    } catch (IOException e) {
      throw new WebSocketException(e);
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
    } catch (IOException ignore)
    { /* ~ */ }
  }

}

