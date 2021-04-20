package com.benefitj.spring.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.*;

/**
 * MQTT配置
 *
 * @author DINGXIUAN
 */
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttOptionsProperty {
  /**
   * 客户端数量，默认1个
   */
  private Integer clientCount = 1;
  /**
   * 客户端ID的前缀
   */
  private String clientIdPrefix = "mqtt-";
  /**
   * 保持连接数
   */
  private Integer keepalive = KEEP_ALIVE_INTERVAL_DEFAULT;
  /**
   * 如果断开，最多保留消息的数量
   */
  private Integer maxInflight = MAX_INFLIGHT_DEFAULT;
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
  private Boolean cleanSession = CLEAN_SESSION_DEFAULT;
  /**
   * 清理会话超时时长
   */
  private Integer connectionTimeout = CONNECTION_TIMEOUT_DEFAULT;
  /**
   * 服务端连接地址，如果有多个，使用逗号分割
   */
  private String serverURIs;
  /**
   * 是否自动重连，默认自动重连
   */
  private Boolean automaticReconnect = true;
  /**
   * 接收端订阅的topic，以逗号(",")分割
   */
  private String subscribeTopics;
  /**
   * 发送端订阅的topic，以逗号(",")分割
   */
  private String publishTopics;
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

  public Integer getClientCount() {
    return clientCount;
  }

  public void setClientCount(Integer clientCount) {
    this.clientCount = clientCount;
  }

  public String getClientIdPrefix() {
    return clientIdPrefix;
  }

  public void setClientIdPrefix(String clientIdPrefix) {
    this.clientIdPrefix = clientIdPrefix;
  }

  public Integer getKeepalive() {
    return keepalive;
  }

  public void setKeepalive(Integer keepalive) {
    this.keepalive = keepalive;
  }

  public Integer getMaxInflight() {
    return maxInflight;
  }

  public void setMaxInflight(Integer maxInflight) {
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

  public Boolean getCleanSession() {
    return cleanSession;
  }

  public void setCleanSession(Boolean cleanSession) {
    this.cleanSession = cleanSession;
  }

  public Integer getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(Integer connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public String getServerURIs() {
    return serverURIs;
  }

  public void setServerURIs(String serverURIs) {
    this.serverURIs = serverURIs;
  }

  public Boolean getAutomaticReconnect() {
    return automaticReconnect;
  }

  public void setAutomaticReconnect(Boolean automaticReconnect) {
    this.automaticReconnect = automaticReconnect;
  }

  public String getSubscribeTopics() {
    return subscribeTopics;
  }

  public void setSubscribeTopics(String subscribeTopics) {
    this.subscribeTopics = subscribeTopics;
  }

  public String getPublishTopics() {
    return publishTopics;
  }

  public void setPublishTopics(String publishTopics) {
    this.publishTopics = publishTopics;
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
}
