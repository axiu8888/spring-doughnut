package com.benefitj.examples.websocket;

import com.alibaba.fastjson.JSON;
import com.benefitj.websocket.SpringServerEndpoint;
import com.benefitj.websocket.SpringWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试 WebSocket
 */
@Component
//@ServerEndpoint("/sockets/javax") // 注册多次会无法运行
@SpringServerEndpoint("/sockets/spring")
public class SimpleWebSocketServer implements SpringWebSocketServer {

  private static final Map<String, WebSocketSession> SOCKETS = new ConcurrentHashMap<>();

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    SOCKETS.put(session.getId(), session);
    logger.info("WebSocket上线, id: {}, 数量: {}", session.getId(), SOCKETS.size());
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    // TextMessage
    // BinaryMessage
    // PingMessage
    // PongMessage

    logger.info("WebSocket消息, id: {}, 数量: {}, msg: {}",
        session.getId(), SOCKETS.size(), JSON.toJSONString(message.getPayload()));
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    logger.info("WebSocket抛出异常, id: {}, 数量: {}, cause: {}",
        session.getId(), SOCKETS.size(), exception.getMessage());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    SOCKETS.remove(session.getId());
    logger.info("WebSocket下线, id: {}, 数量: {}", session.getId(), SOCKETS.size());
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
