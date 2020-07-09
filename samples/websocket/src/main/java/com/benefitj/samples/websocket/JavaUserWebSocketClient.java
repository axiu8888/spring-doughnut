package com.benefitj.samples.websocket;

import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;

/**
 * 注冊WebSocket
 */
@Component
@ServerEndpoint("/websocket/users")
public class JavaUserWebSocketClient {

  private static final Logger log = LoggerFactory.getLogger(JavaUserWebSocketClient.class);

  @OnOpen
  public void onOpen(Session session) {
    log.info("onOpen, session id: {}", session.getId());
  }

  @OnMessage
  public void onTextMessage(Session session, String text, boolean isLast) {
    log.info("onTextMessage, session id: {}, text: {}, isLast: {}", session.getId(), text, isLast);
  }

  @OnMessage
  public void onBinaryMessage(Session session, ByteBuffer buff, boolean isLast) {
    log.info("onBinaryMessage, session id: {}, buff: {}, isLast: {}", session.getId(), HexUtils.toHexString(buff.array()), isLast);
  }

  @OnError
  public void onError(Session session, Throwable e) {
    log.info("onError, session id: {}, error: {}", session.getId(), e.getMessage());
    e.printStackTrace();
  }

  @OnClose
  public void onClose(Session session) {
    log.info("onClose, session id: {}", session.getId());
  }

}
