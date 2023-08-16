package com.benefitj.websocketrelay.message;

/**
 * 消费端的分发器，消息来自于生产端
 */
public class ConsumerMessageDispatcher implements MessageDispatcher {



  @Override
  public Object dispatch(String clientId, Message msg) {
    return null;
  }

}
