package com.benefitj.spring.mqtt;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.*;

/**
 * MQTT配置
 *
 * @author DINGXIUAN
 */
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttOptions {
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
   * 清理会话超时时长，2秒，不要设置超过3秒(有快速自动重连操作)
   */
  private int connectionTimeout = 2;
  /**
   * 发送完成超时，默认3000毫秒
   */
  private long completionTimeout = 3000;
  /**
   * 服务端连接地址，如果有多个，使用逗号分割
   */
  private String serverURIs;
  /**
   * 是否自动重连，默认自动重连
   */
  private boolean autoReconnect = true;
  /**
   * 恢复间隔，默认5秒
   */
  private int reconnectDelay = 5;
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

  public long getCompletionTimeout() {
    return completionTimeout;
  }

  public void setCompletionTimeout(long completionTimeout) {
    this.completionTimeout = completionTimeout;
  }

  public String getServerURIs() {
    return serverURIs;
  }

  public void setServerURIs(String serverURIs) {
    this.serverURIs = serverURIs;
  }

  public boolean isAutoReconnect() {
    return autoReconnect;
  }

  public void setAutoReconnect(boolean autoReconnect) {
    this.autoReconnect = autoReconnect;
  }

  public int getReconnectDelay() {
    return reconnectDelay;
  }

  public void setReconnectDelay(int reconnectDelay) {
    this.reconnectDelay = reconnectDelay;
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


  public MqttConnectOptions toMqttConnectOptions() {
    return toMqttConnectOptions(this);
  }

  public static MqttConnectOptions toMqttConnectOptions(MqttOptions options) {
    MqttConnectOptions connectOptions = new MqttConnectOptions();
    // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
    // 这里设置为true表示每次连接到服务器都以新的身份连接
    connectOptions.setCleanSession(options.isCleanSession());
    // 设置连接的用户名
    connectOptions.setUserName(options.getUsername());
    // 设置连接的密码
    connectOptions.setPassword((StringUtils.getIfBlank(options.getPassword(), () -> "").toCharArray()));
    connectOptions.setServerURIs(StringUtils.split(options.getServerURIs(), ","));
    // 设置超时时间 单位为秒
    connectOptions.setConnectionTimeout(options.getConnectionTimeout());
    // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
    connectOptions.setKeepAliveInterval(options.getKeepalive());
    // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
    MqttOptions.Will will = options.getWill();
    if (StringUtils.isNotBlank(will.getPayload())) {
      connectOptions.setWill(will.getTopic(), will.getPayload().getBytes(StandardCharsets.UTF_8), will.getQos(), will.isRetained());
    }
    connectOptions.setAutomaticReconnect(false); // 不自动重连，使用自定义的自动重连
    connectOptions.setMaxReconnectDelay(options.getReconnectDelay() * 1000);
    // 最大发送数量
    connectOptions.setMaxInflight(options.getMaxInflight());
    return connectOptions;
  }
}
