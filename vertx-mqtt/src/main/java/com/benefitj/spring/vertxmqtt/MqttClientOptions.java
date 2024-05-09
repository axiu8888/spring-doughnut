package com.benefitj.spring.vertxmqtt;

import java.util.concurrent.TimeUnit;

public class MqttClientOptions {

  /**
   * 远程主机地址
   */
  private String host;
  /**
   * 端口，默认 1883
   */
  private Integer port = 1883;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * 客户端前缀，默认 mqtt-
   */
  private String clientIdPrefix = "mqtt-";
  /**
   * 是否清理 session，默认清理
   */
  private boolean cleanSession = io.vertx.mqtt.MqttClientOptions.DEFAULT_CLEAN_SESSION;
  /**
   * 遗嘱的主题
   */
  private String willTopic;
  /**
   * 遗嘱的消息
   */
  private String willMessage;
  /**
   * 是否启用遗嘱，默认不起用
   */
  private boolean willFlag = io.vertx.mqtt.MqttClientOptions.DEFAULT_WILL_FLAG;
  /**
   * 遗嘱的服务质量，默认0
   */
  private int willQos = io.vertx.mqtt.MqttClientOptions.DEFAULT_WILL_QOS;
  /**
   * 遗嘱是否保留，默认false
   */
  private boolean willRetain = io.vertx.mqtt.MqttClientOptions.DEFAULT_WILL_RETAIN;
  /**
   * 是否自动保持存活，默认启用
   */
  private boolean autoKeepAlive = true;
  /**
   * 保持存活的间隔，默认30秒
   */
  private int keepAliveInterval = io.vertx.mqtt.MqttClientOptions.DEFAULT_KEEP_ALIVE_INTERVAL;
  /**
   * 队列中最多缓存发送的数量，默认30
   */
  private int maxInflightQueue = 30;
  /**
   * 最大消息大小，默认-1，不受限
   */
  private int maxMessageSize = io.vertx.mqtt.MqttClientOptions.DEFAULT_MAX_MESSAGE_SIZE;
  /**
   * 确认超时时长，默认-1，不超时
   */
  private int ackTimeout = io.vertx.mqtt.MqttClientOptions.DEFAULT_ACK_TIMEOUT;

  /**
   * 自动重连
   */
  private AutoReconnect autoReconnect = new AutoReconnect();

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
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

  public String getClientIdPrefix() {
    return clientIdPrefix;
  }

  public void setClientIdPrefix(String clientIdPrefix) {
    this.clientIdPrefix = clientIdPrefix;
  }

  public boolean isAutoKeepAlive() {
    return autoKeepAlive;
  }

  public void setAutoKeepAlive(boolean autoKeepAlive) {
    this.autoKeepAlive = autoKeepAlive;
  }

  public String getWillTopic() {
    return willTopic;
  }

  public void setWillTopic(String willTopic) {
    this.willTopic = willTopic;
  }

  public String getWillMessage() {
    return willMessage;
  }

  public void setWillMessage(String willMessage) {
    this.willMessage = willMessage;
  }

  public boolean isCleanSession() {
    return cleanSession;
  }

  public void setCleanSession(boolean cleanSession) {
    this.cleanSession = cleanSession;
  }

  public boolean isWillFlag() {
    return willFlag;
  }

  public void setWillFlag(boolean willFlag) {
    this.willFlag = willFlag;
  }

  public int getWillQos() {
    return willQos;
  }

  public void setWillQos(int willQos) {
    this.willQos = willQos;
  }

  public boolean isWillRetain() {
    return willRetain;
  }

  public void setWillRetain(boolean willRetain) {
    this.willRetain = willRetain;
  }

  public int getKeepAliveInterval() {
    return keepAliveInterval;
  }

  public void setKeepAliveInterval(int keepAliveInterval) {
    this.keepAliveInterval = keepAliveInterval;
  }

  public int getMaxInflightQueue() {
    return maxInflightQueue;
  }

  public void setMaxInflightQueue(int maxInflightQueue) {
    this.maxInflightQueue = maxInflightQueue;
  }

  public int getMaxMessageSize() {
    return maxMessageSize;
  }

  public void setMaxMessageSize(int maxMessageSize) {
    this.maxMessageSize = maxMessageSize;
  }

  public int getAckTimeout() {
    return ackTimeout;
  }

  public void setAckTimeout(int ackTimeout) {
    this.ackTimeout = ackTimeout;
  }

  public AutoReconnect getAutoReconnect() {
    return autoReconnect;
  }

  public void setAutoReconnect(AutoReconnect autoReconnect) {
    this.autoReconnect = autoReconnect;
  }

  public static class AutoReconnect {

    /**
     * 重连间隔
     */
    private int period = 30;
    /**
     * 重连的时间单位
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    /**
     * 是否自动重连
     */
    private boolean auto = true;

    public int getPeriod() {
      return period;
    }

    public void setPeriod(int period) {
      this.period = period;
    }

    public TimeUnit getTimeUnit() {
      return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
      this.timeUnit = timeUnit;
    }

    public boolean isAuto() {
      return auto;
    }

    public void setAuto(boolean auto) {
      this.auto = auto;
    }
  }


}
