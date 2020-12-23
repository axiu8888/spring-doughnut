package com.benefitj.samples.websocket;

import com.benefitj.spring.websocket.JavaxWebSocket;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.nio.ByteBuffer;

/**
 * 注冊WebSocket
 */
//@Component
//@ServerEndpoint("/websocket/users")
public class JavaxUserWebSocketClient extends JavaxWebSocket {

  private static final Logger log = LoggerFactory.getLogger(JavaxUserWebSocketClient.class);

  @Override
  public void onOpen(Session session) {
    log.info("onOpen, session id: {}", session.getId());
  }

  @Override
  public void onTextMessage(Session session, String text, boolean isLast) {
    log.info("onTextMessage, session id: {}, text: {}, isLast: {}", session.getId(), text, isLast);
  }

  @Override
  public void onBinaryMessage(Session session, ByteBuffer buff, boolean isLast) {
    log.info("onBinaryMessage, session id: {}, buff: {}, isLast: {}", session.getId(), HexUtils.toHexString(buff.array()), isLast);
  }

  @Override
  public void onClose(Session session, CloseReason reason) {
    log.info("onClose, session id: {}", session.getId());
  }

  @Override
  public void onError(Session session, Throwable e) {
    log.info("onError, session id: {}, error: {}", session.getId(), e.getMessage());
    e.printStackTrace();
  }

}
