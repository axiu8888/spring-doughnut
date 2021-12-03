package com.benefitj.spring.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.*;

/**
 * MQTT配置
 *
 * @author DINGXIUAN
 */
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttProperty {
  /**
   * 保持连接数
   */
  private int keepalive = KEEP_ALIVE_INTERVAL_DEFAULT;
  /**
   * 如果断开，最多保留消息的数量
   */
  private int maxInflight = MAX_INFLIGHT_DEFAULT;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * 清理会话
   */
  private boolean cleanSession = CLEAN_SESSION_DEFAULT;
  /**
   * 清理会话超时时长
   */
  private int connectionTimeout = CONNECTION_TIMEOUT_DEFAULT;
  /**
   * 服务端连接地址，如果有多个，使用逗号分割
   */
  private String serverURIs;
  /**
   * 是否自动重连，默认自动重连
   */
  private boolean automaticReconnect = true;
  /**
   * 发送完成超时，默认3秒
   */
  private long completionTimeout = 3000;
  /**
   * 恢复间隔，默认3秒
   */
  private int recoveryInterval = 3000;
  /**
   * 服务质量，默认 0
   */
  private int qos = 0;
  /**
   * 主题
   */
  private Will will = new Will();

  public int getKeepalive() {
    return keepalive;
  }

  public void setKeepalive(int keepalive) {
    this.keepalive = keepalive;
  }

  public int getMaxInflight() {
    return maxInflight;
  }

  public void setMaxInflight(int maxInflight) {
    this.maxInflight = maxInflight;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isCleanSession() {
    return cleanSession;
  }

  public void setCleanSession(boolean cleanSession) {
    this.cleanSession = cleanSession;
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public String getServerURIs() {
    return serverURIs;
  }

  public void setServerURIs(String serverURIs) {
    this.serverURIs = serverURIs;
  }

  public boolean isAutomaticReconnect() {
    return automaticReconnect;
  }

  public void setAutomaticReconnect(boolean automaticReconnect) {
    this.automaticReconnect = automaticReconnect;
  }

  public long getCompletionTimeout() {
    return completionTimeout;
  }

  public void setCompletionTimeout(long completionTimeout) {
    this.completionTimeout = completionTimeout;
  }

  public int getRecoveryInterval() {
    return recoveryInterval;
  }

  public void setRecoveryInterval(int recoveryInterval) {
    this.recoveryInterval = recoveryInterval;
  }

  public int getQos() {
    return qos;
  }

  public void setQos(int qos) {
    this.qos = qos;
  }

  public Will getWill() {
    return will;
  }

  public void setWill(Will will) {
    this.will = will;
  }

  public static class Will {

    private String topic;
    private String payload;
    private int qos;
    private boolean retained;

    public String getTopic() {
      return topic;
    }

    public void setTopic(String topic) {
      this.topic = topic;
    }

    public String getPayload() {
      return payload;
    }

    public void setPayload(String payload) {
      this.payload = payload;
    }

    public int getQos() {
      return qos;
    }

    public void setQos(int qos) {
      this.qos = qos;
    }

    public boolean isRetained() {
      return retained;
    }

    public void setRetained(boolean retained) {
      this.retained = retained;
    }
  }
}
