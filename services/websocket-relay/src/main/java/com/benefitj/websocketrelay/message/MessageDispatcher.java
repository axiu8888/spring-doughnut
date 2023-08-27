package com.benefitj.websocketrelay.message;

import com.benefitj.spring.websocket.WebSocket;

/**
 * 消息分发器
 */
public interface MessageDispatcher {

  /**
   * 分发消息
   *
   * @param socket 消息来源的客户端
   * @param msg    消息
   */
  void dispatch(WebSocket socket, Message msg);

}
