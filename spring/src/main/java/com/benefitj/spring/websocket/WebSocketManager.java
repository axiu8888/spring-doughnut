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
    final Map<String, WebSocket> sockets = new ConcurrentHashMap<>(20);
    return () -> sockets;
  }

}
