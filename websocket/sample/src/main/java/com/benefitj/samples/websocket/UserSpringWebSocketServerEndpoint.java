package com.benefitj.samples.websocket;

import com.benefitj.spring.websocket.SpringServerEndpoint;
import com.benefitj.spring.websocket.SpringWebSocketClient;
import com.benefitj.spring.websocket.SpringWebSocketServerEndpoint;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注冊WebSocket
 */
@Component
@SpringServerEndpoint("/websocket/users")
public class UserSpringWebSocketServerEndpoint extends SpringWebSocketServerEndpoint {

  private static final Logger log = LoggerFactory.getLogger(UserSpringWebSocketServerEndpoint.class);

  public static final Map<String, UserSpringWebSocketClient> SOCKETS = new ConcurrentHashMap<>();

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

  /**
   * 创建 WebSocket客户端
   *
   * @param session 会话
   * @return 返回客户端
   */
  @Override
  public SpringWebSocketClient createClient(WebSocketSession session) {
    return new UserSpringWebSocketClient(session);
  }


  static class UserSpringWebSocketClient extends SpringWebSocketClient {

    public UserSpringWebSocketClient(WebSocketSession session) {
      super(session);
    }

    @Override
    public void onOpen() {
      SOCKETS.put(getId(), this);
      log.info("onOpen, session id: {}, size[{}]", getId(), SOCKETS.size());
    }

    @Override
    public void onTextMessage(String text, boolean isLast) {
      log.info("onTextMessage, session id: {}, text: {}", getId(), text);
    }

    @Override
    public void onBinaryMessage(ByteBuffer buffer, boolean isLast) {
      log.info("onBinaryMessage, session id: {}, buffer: {}",
          getId(), HexUtils.toHexString(buffer.array()));
    }


    @Override
    public void onError(Throwable e) {
      log.info("onError, session id: {}, error: {}", getId(), e.getMessage());
      e.printStackTrace();
    }

    @Override
    public void onClose(String reason, int code) {
      SOCKETS.remove(getId());
      log.info("onClose, session id: {}, reason: {}, code: {}, size[{}]", getId(), reason, code, SOCKETS.size());
    }

  }
}

