package com.benefitj.spring.websocket;

import com.benefitj.core.functions.WrappedMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket管理器
 */
public interface WebSocketManager extends WrappedMap<String, WebSocket> {

  /**
   * 创建WebSocket管理器
   */
  static WebSocketManager newInstance() {
    return new WebSocketManagerImpl();
  }

  /**
   * WebSocket管理器实现
   */
  class WebSocketManagerImpl implements WebSocketManager {

    /**
     * Sockets
     */
    private final Map<String, WebSocket> sockets = new ConcurrentHashMap<>();

    @Override
    public Map<String, WebSocket> getOriginal() {
      return sockets;
    }

  }

}
