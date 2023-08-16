package com.benefitj.websocketrelay.message;

/**
 * 消息分发器
 */
public interface MessageDispatcher {

  /**
   * 分发消息
   *
   * @param clientId 客户端
   * @param msg      消息
   * @return 返回结果
   */
  Object dispatch(String clientId, Message msg);

}
