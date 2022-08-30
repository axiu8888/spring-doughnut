package com.benefitj.spring.mqtt.publisher;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

/**
 * MQTT发送
 */
public interface IMqttPublisher {
  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void publish(String topic, String payload) {
    publish(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void publishAsync(String topic, String payload) {
    publishAsync(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void publish(String topic, byte[] payload) {
    publish(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void publishAsync(String topic, byte[] payload) {
    publishAsync(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void publish(String[] topics, String payload) {
    publish(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void publishAsync(String[] topics, String payload) {
    publishAsync(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void publish(String[] topics, byte[] payload) {
    publish(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void publishAsync(String[] topics, byte[] payload) {
    publishAsync(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publish(String topic, String payload, int qos) {
    publish(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publishAsync(String topic, String payload, int qos) {
    publishAsync(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publish(String topic, byte[] payload, int qos) {
    publish(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publishAsync(String topic, byte[] payload, int qos) {
    publishAsync(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publish(String[] topics, String payload, int qos) {
    publish(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publishAsync(String[] topics, String payload, int qos) {
    publishAsync(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publish(String[] topics, byte[] payload, int qos) {
    publish(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publishAsync(String[] topics, byte[] payload, int qos) {
    publishAsync(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publish(String topic, String payload, int qos, boolean retained) {
    publish(topic, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publishAsync(String topic, String payload, int qos, boolean retained) {
    publishAsync(topic, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publish(String topic, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    publish(topic, msg);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publishAsync(String topic, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    publishAsync(topic, msg);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publish(String[] topics, String payload, int qos, boolean retained) {
    publish(topics, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publishAsync(String[] topics, String payload, int qos, boolean retained) {
    publishAsync(topics, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publish(String[] topics, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    publish(topics, msg);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publishAsync(String[] topics, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    publishAsync(topics, msg);
  }


  /**
   * 发送
   *
   * @param topics 主题
   * @param msg    消息
   */
  default void publish(String[] topics, MqttMessage msg) throws MqttPublishException {
    for (String topic : topics) {
      publish(topic, msg);
    }
  }

  /**
   * 发送
   *
   * @param topics 主题
   * @param msg    消息
   */
  default void publishAsync(String[] topics, MqttMessage msg) {
    for (String topic : topics) {
      publishAsync(topic, msg);
    }
  }

  /**
   * 发送
   *
   * @param topic 主题
   * @param msg   消息
   */
  void publish(String topic, MqttMessage msg) throws MqttPublishException;

  /**
   * 发送
   *
   * @param topic 主题
   * @param msg   消息
   */
  void publishAsync(String topic, MqttMessage msg);

}
