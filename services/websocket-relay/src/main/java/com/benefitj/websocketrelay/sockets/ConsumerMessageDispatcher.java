package com.benefitj.websocketrelay.sockets;

import com.benefitj.spring.websocket.WebSocket;
import com.benefitj.websocketrelay.message.Message;
import com.benefitj.websocketrelay.message.MessageDispatcher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费端的分发器，消息来自于生产端
 */
@Component
public class ConsumerMessageDispatcher implements MessageDispatcher {

  final Map<String, WebSocket> sockets = new ConcurrentHashMap<>();


  @Override
  public void dispatch(WebSocket socket, Message msg) {
    for (Map.Entry<String, WebSocket> entry : sockets.entrySet()) {
      WebSocket ws = entry.getValue();
    }
  }

}
