package com.benefitj.spring.websocket;

import java.nio.ByteBuffer;

public interface WebSocketClient<Session> {

  /**
   * 客户端打开
   */
  void onOpen();

  /**
   * 处理文本数据
   *
   * @param text   数据
   * @param isLast 是否是最后的数据
   */
  void onTextMessage(String text, boolean isLast);

  /**
   * 处理文本数据
   *
   * @param buffer 数据
   * @param isLast 是否是最后的数据
   */
  void onBinaryMessage(ByteBuffer buffer, boolean isLast);

  /**
   * 出现错误
   *
   * @param e 异常
   */
  void onError(Throwable e);

  /**
   * 客户端关闭
   *
   * @param reason 原因
   * @param code   状态码
   */
  void onClose(String reason, int code);

  /**
   * 获取Session
   */
  Session getSession();

  /**
   * 获取 Session ID
   */
  String getId();

  /**
   * 发送文本数据
   *
   * @param text 数据
   */
  void sendText(String text);

  /**
   * 发送字节数据
   *
   * @param data 数据
   */
  void sendBinary(byte[] data);

  /**
   * 发送字节数据
   *
   * @param data 数据
   */
  void sendBinary(ByteBuffer data);

  /**
   * 当前的WebSocket是否处于打开状态
   */
  boolean isOpen();

  /**
   * 关闭当前会话
   */
  void close();


  /**
   * 关闭
   */
  public static void closeAll(AutoCloseable... cs) {
    for (AutoCloseable c : cs) {
      try {
        if (c != null) {
          c.close();
        }
      } catch (Exception e) {/* ignore */}
    }
  }


}
