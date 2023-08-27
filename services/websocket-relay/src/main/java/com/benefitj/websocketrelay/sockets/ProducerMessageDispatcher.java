package com.benefitj.websocketrelay.sockets;

import com.benefitj.spring.websocket.WebSocket;
import com.benefitj.websocketrelay.message.Message;
import com.benefitj.websocketrelay.message.MessageDispatcher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生产端的分发器，消息来自于消费端
 */
@Component
public class ProducerMessageDispatcher implements MessageDispatcher {

  final Map<String, WebSocket> sockets = new ConcurrentHashMap<>();

  @Override
  public void dispatch(WebSocket producer, Message msg) {
    for (Map.Entry<String, WebSocket> entry : sockets.entrySet()) {
      WebSocket consumer = entry.getValue();
      consumer.send(msg.getJson().toJSONString());
    }
  }

}
