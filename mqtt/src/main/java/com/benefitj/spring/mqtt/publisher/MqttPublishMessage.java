package com.benefitj.spring.mqtt.publisher;

import java.util.Arrays;

/**
 * MQTT消息
 */
public class MqttPublishMessage {

  /**
   * 主题
   */
  private String[] topics;
  /**
   * 有效数据
   */
  private byte[] payload;
  /**
   * 服务质量
   */
  private int qos = 1;
  /**
   * 是否保留
   */
  private boolean retained = false;
  /**
   *
   */
  private boolean dup = false;
  /**
   * 消息ID
   */
  private int messageId;

  public MqttPublishMessage() {
  }

  public MqttPublishMessage(String topic) {
    this(new String[]{topic});
  }

  public MqttPublishMessage(String[] topics) {
    this.topics = topics;
  }

  public MqttPublishMessage(String topic, byte[] payload) {
    this(new String[]{topic}, payload);
  }

  public MqttPublishMessage(String[] topics, byte[] payload) {
    this.topics = topics;
    this.payload = payload;
  }

  public MqttPublishMessage(String[] topics, byte[] payload, int qos, int messageId) {
    this.topics = topics;
    this.payload = payload;
    this.qos = qos;
    this.messageId = messageId;
  }

  public String[] getTopics() {
    return topics;
  }

  public void setTopics(String[] topics) {
    this.topics = topics;
  }

  public byte[] getPayload() {
    return payload;
  }

  public void setPayload(byte[] payload) {
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

  public boolean isDup() {
    return dup;
  }

  public void setDup(boolean dup) {
    this.dup = dup;
  }

  public int getMessageId() {
    return messageId;
  }

  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append(getClass().getSimpleName())
        .append("(")
        .append(Arrays.toString(topics))
        .append("#").append(messageId)
        .append("#").append(qos)
        .append("#").append(retained)
        .append("#").append(dup)
        .append(")")
        .toString();
  }
}
