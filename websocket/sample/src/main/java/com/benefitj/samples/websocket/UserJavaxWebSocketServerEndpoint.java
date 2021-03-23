package com.benefitj.samples.websocket;

import com.benefitj.spring.websocket.JavaxWebSocketClient;
import com.benefitj.spring.websocket.JavaxWebSocketServerEndpoint;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.nio.ByteBuffer;

/**
 * 注冊WebSocket
 */
//@Component
//@ServerEndpoint("/websocket/users")
public class UserJavaxWebSocketServerEndpoint extends JavaxWebSocketServerEndpoint {

  private static final Logger log = LoggerFactory.getLogger(UserJavaxWebSocketServerEndpoint.class);

  /**
   * 创建 WebSocket客户端
   *
   * @param session 会话
   * @return 返回客户端
   */
  @Override
  public JavaxWebSocketClient createClient(Session session) {
    return new UserJavaxWebSocketClient(session);
  }

  static class UserJavaxWebSocketClient extends JavaxWebSocketClient {

    public UserJavaxWebSocketClient(Session session) {
      super(session);
    }

    @Override
    public void onOpen() {
      log.info("onOpen, session id: {}", getId());
    }

    @Override
    public void onTextMessage(String text, boolean isLast) {
      log.info("onTextMessage, session id: {}, text: {}, isLast: {}", getId(), text, isLast);
    }

    @Override
    public void onBinaryMessage(ByteBuffer buff, boolean isLast) {
      log.info("onBinaryMessage, session id: {}, buff: {}, isLast: {}", getId(), HexUtils.toHexString(buff.array()), isLast);
    }

    @Override
    public void onClose(String reason, int code) {
      log.info("onClose, session id: {}, reason: {}, code: {}", getId(), reason, code);
    }

    @Override
    public void onError(Throwable e) {
      log.info("onError, session id: {}, error: {}", getId(), e.getMessage());
      e.printStackTrace();
    }
  }

}
