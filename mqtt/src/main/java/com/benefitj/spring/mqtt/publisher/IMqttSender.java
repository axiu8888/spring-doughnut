package com.benefitj.spring.mqtt.publisher;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;

/**
 * MQTT发送
 */
public interface IMqttSender {

  /**
   * MQTT客户端
   */
  IMqttClient getClient();

  /**
   * 调度器
   */
  Executor getExecutor();

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void send(String topic, String payload) {
    send(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void sendAsync(String topic, String payload) {
    sendAsync(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void send(String topic, byte[] payload) {
    send(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void sendAsync(String topic, byte[] payload) {
    sendAsync(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void send(String[] topics, String payload) {
    send(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void sendAsync(String[] topics, String payload) {
    sendAsync(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void send(String[] topics, byte[] payload) {
    send(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void sendAsync(String[] topics, byte[] payload) {
    sendAsync(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void send(String topic, String payload, int qos) {
    send(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void sendAsync(String topic, String payload, int qos) {
    sendAsync(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void send(String topic, byte[] payload, int qos) {
    send(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void sendAsync(String topic, byte[] payload, int qos) {
    sendAsync(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void send(String[] topics, String payload, int qos) {
    send(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void sendAsync(String[] topics, String payload, int qos) {
    sendAsync(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void send(String[] topics, byte[] payload, int qos) {
    send(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void sendAsync(String[] topics, byte[] payload, int qos) {
    sendAsync(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void send(String topic, String payload, int qos, boolean retained) {
    send(topic, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void sendAsync(String topic, String payload, int qos, boolean retained) {
    sendAsync(topic, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void send(String topic, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    send(topic, msg);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void sendAsync(String topic, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    sendAsync(topic, msg);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void send(String[] topics, String payload, int qos, boolean retained) {
    send(topics, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void sendAsync(String[] topics, String payload, int qos, boolean retained) {
    sendAsync(topics, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void send(String[] topics, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    send(topics, msg);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void sendAsync(String[] topics, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    sendAsync(topics, msg);
  }

  /**
   * 发送
   *
   * @param topic 主题
   * @param msg   消息
   */
  default void send(String topic, MqttMessage msg) throws MqttPublishException {
    IMqttClient c = getClient();
    if (c != null) {
      try {
        c.publish(topic, msg);
      } catch (MqttException e) {
        throw new MqttPublishException(e);
      }
    }
  }

  /**
   * 发送
   *
   * @param topic 主题
   * @param msg   消息
   */
  default void sendAsync(String topic, MqttMessage msg) {
    final IMqttClient c = getClient();
    if (c != null) {
      getExecutor().execute(() -> {
        try {
          c.publish(topic, msg);
        } catch (MqttException e) {
          throw new MqttPublishException(e);
        }
      });
    }
  }

  /**
   * 发送
   *
   * @param topics 主题
   * @param msg    消息
   */
  default void send(String[] topics, MqttMessage msg) throws MqttPublishException {
    IMqttClient c = getClient();
    if (c != null) {
      try {
        for (String topic : topics) {
          c.publish(topic, msg);
        }
      } catch (MqttException e) {
        throw new MqttPublishException(e);
      }
    }
  }

  /**
   * 发送
   *
   * @param topics 主题
   * @param msg    消息
   */
  default void sendAsync(String[] topics, MqttMessage msg) {
    final IMqttClient c = getClient();
    if (c != null) {
      getExecutor().execute(() -> {
        try {
          for (String topic : topics) {
            c.publish(topic, msg);
          }
        } catch (MqttException e) {
          throw new MqttPublishException(e);
        }
      });
    }
  }

}
