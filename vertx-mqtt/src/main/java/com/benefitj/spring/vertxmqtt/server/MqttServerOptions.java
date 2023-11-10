package com.benefitj.spring.vertxmqtt.server;

public class MqttServerOptions {

  String host = "0.0.0.0";
  /**
   * TCP端口
   */
  int tcpPort = 1883;
  /**
   * 是否启用 TCP 服务
   */
  boolean tcpEnable = true;
  /**
   * WebSocket端口
   */
  int wsPort = 8083;
  /**
   * WebSocket最大帧
   */
  int wsMaxFrameSize = io.vertx.mqtt.MqttServerOptions.DEFAULT_WEB_SOCKET_MAX_FRAME_SIZE;
  /**
   * 是否启用WebSocket服务
   */
  boolean wsEnable = false;
  /**
   * 最大消息体长度
   */
  int maxMessageSize = 1024 << 10;
  /**
   * 驱逐过时或冲突的session
   */
  boolean dislodgeSession = true;
  /**
   * 是否启用SSL
   */
  boolean ssl = false;
  /**
   * KEY路径
   */
  String keyPath;
  /**
   * 证书路径
   */
  String certPath;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getTcpPort() {
    return tcpPort;
  }

  public void setTcpPort(int tcpPort) {
    this.tcpPort = tcpPort;
  }

  public int getWsPort() {
    return wsPort;
  }

  public void setWsPort(int wsPort) {
    this.wsPort = wsPort;
  }

  public boolean isTcpEnable() {
    return tcpEnable;
  }

  public void setTcpEnable(boolean tcpEnable) {
    this.tcpEnable = tcpEnable;
  }

  public boolean isWsEnable() {
    return wsEnable;
  }

  public void setWsEnable(boolean wsEnable) {
    this.wsEnable = wsEnable;
  }

  public int getWsMaxFrameSize() {
    return wsMaxFrameSize;
  }

  public void setWsMaxFrameSize(int wsMaxFrameSize) {
    this.wsMaxFrameSize = wsMaxFrameSize;
  }

  public int getMaxMessageSize() {
    return maxMessageSize;
  }

  public void setMaxMessageSize(int maxMessageSize) {
    this.maxMessageSize = maxMessageSize;
  }

  public boolean isDislodgeSession() {
    return dislodgeSession;
  }

  public void setDislodgeSession(boolean dislodgeSession) {
    this.dislodgeSession = dislodgeSession;
  }

  public boolean isSsl() {
    return ssl;
  }

  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  public String getKeyPath() {
    return keyPath;
  }

  public void setKeyPath(String keyPath) {
    this.keyPath = keyPath;
  }

  public String getCertPath() {
    return certPath;
  }

  public void setCertPath(String certPath) {
    this.certPath = certPath;
  }
}
