package com.benefitj.spring.mqtt;

import org.springframework.messaging.MessageHeaders;

/**
 * MQTT消息头
 */
public class MqttHeaders {

  public static final String MQTT_ID = "mqtt_id";
  public static final String MQTT_RECEIVEDTOPIC = "mqtt_receivedTopic";
  public static final String MQTT_RECEIVEDQOS = "mqtt_receivedQos";
  public static final String MQTT_RECEIVEDRETAINED = "mqtt_receivedRetained";
  public static final String MQTT_DUPLICATE = "mqtt_duplicate";

  /**
   * 转换消息首部
   *
   * @param headers MQTT消息首部
   * @return 返回 MQTTHeaders
   */
  public static MqttHeaders of(MessageHeaders headers) {
    MqttHeaders mh = new MqttHeaders();
    mh.setId(String.valueOf(headers.getId()));
    mh.setMqttId(headers.get(MQTT_ID, Integer.class));
    mh.setReceivedTopic(headers.get(MQTT_RECEIVEDTOPIC, String.class));
    Integer qos = headers.get(MQTT_RECEIVEDQOS, Integer.class);
    mh.setReceivedQos(qos != null ? qos : 0);
    mh.setReceivedRetained(Boolean.TRUE.equals(headers.get(MQTT_RECEIVEDRETAINED, Boolean.class)));
    mh.setDuplicate(Boolean.TRUE.equals(headers.get(MQTT_DUPLICATE, Boolean.class)));
    mh.setTimestamp(headers.getTimestamp());
    return mh;
  }

  /**
   * ID
   */
  private String id;
  /**
   * 消息ID
   */
  private Integer mqttId;
  /**
   * topic
   */
  private String receivedTopic;
  /**
   * 是否保留
   */
  private boolean receivedRetained;
  /**
   * 是否重复
   */
  private boolean duplicate;
  /**
   * QoS
   */
  private int receivedQos;
  /**
   * 时间戳
   */
  private Long timestamp;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getMqttId() {
    return mqttId;
  }

  public void setMqttId(Integer mqttId) {
    this.mqttId = mqttId;
  }

  public String getReceivedTopic() {
    return receivedTopic;
  }

  public void setReceivedTopic(String receivedTopic) {
    this.receivedTopic = receivedTopic;
  }

  public boolean isReceivedRetained() {
    return receivedRetained;
  }

  public void setReceivedRetained(boolean receivedRetained) {
    this.receivedRetained = receivedRetained;
  }

  public boolean isDuplicate() {
    return duplicate;
  }

  public void setDuplicate(boolean duplicate) {
    this.duplicate = duplicate;
  }

  public int getReceivedQos() {
    return receivedQos;
  }

  public void setReceivedQos(int receivedQos) {
    this.receivedQos = receivedQos;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append(getClass().getSimpleName())
        .append("(")
        .append("id=").append(id)
        .append(", mqttId=").append(mqttId)
        .append(", receivedTopic=").append(receivedTopic)
        .append(", receivedRetained=").append(receivedRetained)
        .append(", duplicate=").append(duplicate)
        .append(", receivedQos=").append(receivedQos)
        .append(", timestamp=").append(timestamp)
        .append(")")
        .toString();
  }
}
