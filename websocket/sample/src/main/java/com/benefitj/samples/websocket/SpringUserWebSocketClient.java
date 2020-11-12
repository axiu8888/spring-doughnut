package com.benefitj.samples.websocket;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.nio.ByteBuffer;

/**
 * 注冊WebSocket
 */
// "/websocket/users"
public class SpringUserWebSocketClient implements WebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(SpringUserWebSocketClient.class);

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    log.info("afterConnectionEstablished, session id: {}", session.getId());
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    if (message instanceof BinaryMessage) {
      ByteBuffer buff = ((BinaryMessage) message).getPayload();
      log.info("handleMessage binary, session id: {}, buff: {}",
          session.getId(), HexUtils.toHexString(buff.array()));
    } else {
      String text = ((TextMessage) message).getPayload();
      log.info("handleMessage text, session id: {}, text: {}", session.getId(), text);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable e) throws Exception {
    log.info("handleTransportError, session id: {}, error: {}", session.getId(), e.getMessage());
    e.printStackTrace();
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    log.info("afterConnectionClosed, session id: {}, closeStatus: {}", session.getId(), closeStatus);
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }
}
