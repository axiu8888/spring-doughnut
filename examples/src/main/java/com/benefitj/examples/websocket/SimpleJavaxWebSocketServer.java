package com.benefitj.examples.websocket;

import com.benefitj.websocket.JavaxWebSocketServer;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/sockets/javax")
public class SimpleJavaxWebSocketServer extends JavaxWebSocketServer {

  private static final Map<String, Session> SOCKETS = new ConcurrentHashMap<>();

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void onOpen(Session session) {
    SOCKETS.put(session.getId(), session);
    logger.info("WebSocket上线, id: {}, 数量: {}", session.getId(), SOCKETS.size());
  }

  @Override
  public void onTextMessage(Session session, String text, boolean isLast) {
    logger.info("WebSocket消息(Text), id: {}, 数量: {}, msg: {}",
        session.getId(), SOCKETS.size(), text);
  }

  @Override
  public void onBinaryMessage(Session session, ByteBuffer buffer, boolean isLast) {
    logger.info("WebSocket消息(Binary), id: {}, 数量: {}, msg: {}",
        session.getId(), SOCKETS.size(), HexUtils.toHexString(buffer.array()));
  }

  @Override
  public void onClose(Session session, CloseReason reason) {
    SOCKETS.remove(session.getId());
    logger.info("WebSocket下线, id: {}, 数量: {}", session.getId(), SOCKETS.size());
  }

  @Override
  public void onError(Session session, Throwable e) {
    logger.info("WebSocket抛出异常, id: {}, 数量: {}, cause: {}",
        session.getId(), SOCKETS.size(), e.getMessage());
  }
}
