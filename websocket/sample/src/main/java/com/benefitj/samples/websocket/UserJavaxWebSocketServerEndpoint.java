package com.benefitj.samples.websocket;

import com.benefitj.spring.websocket.JavaxWebSocketClient;
import com.benefitj.spring.websocket.JavaxWebSocketServerEndpoint;
import org.apache.tomcat.util.buf.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注冊WebSocket
 */
//@Component
//@ServerEndpoint("/websocket/users")
public class UserJavaxWebSocketServerEndpoint extends JavaxWebSocketServerEndpoint {

  private static final Logger log = LoggerFactory.getLogger(UserJavaxWebSocketServerEndpoint.class);

  public static final Map<String, UserJavaxWebSocketClient> SOCKETS = new ConcurrentHashMap<>();

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
      SOCKETS.put(getId(), this);
      log.info("onOpen, session id: {}, size[{}]", getId(), SOCKETS.size());
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
