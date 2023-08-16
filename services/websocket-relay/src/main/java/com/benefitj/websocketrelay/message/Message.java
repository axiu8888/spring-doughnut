package com.benefitj.websocketrelay.message;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.benefitj.spring.websocket.WebSocket;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 消息
 */
@SuperBuilder
@Data
public class Message {

  @JSONField(serialize = false, deserialize = false)
  @JsonIgnore
  WebSocket socket;

  @JSONField(serialize = false, deserialize = false)
  @JsonIgnore
  JSONObject json;

  /**
   * 客户端IP
   */
  String clientId;
  /**
   * 消息类型
   */
  Type type;
  /**
   * 消息 ID
   */
  String id;
  /**
   * 方法： Bluetooth.scan
   */
  String method;
  /**
   * 参数
   */
  JSONObject params;
  /**
   * 结果
   */
  JSONObject result;
  /**
   * 错误
   */
  JSONObject error;

  public enum Type {
    /**
     * 服务端
     */
    server,
    /**
     * 消费者
     */
    consumer,
    /**
     * 生产者
     */
    producer,
  }

}
