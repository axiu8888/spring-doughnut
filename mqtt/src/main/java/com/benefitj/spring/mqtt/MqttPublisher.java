package com.benefitj.spring.mqtt;

import com.benefitj.core.ReflectUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

import java.nio.charset.StandardCharsets;

/**
 * MQTT发送端
 */
public class MqttPublisher {

  /**
   * MQTT适配器
   */
  private MqttPahoMessageDrivenChannelAdapter channelAdapter;
  /**
   * 客户端
   */
  private volatile IMqttClient client;
  /**
   * 上次获取的时间
   */
  private volatile long obtainTime;

  public MqttPublisher(MqttPahoMessageDrivenChannelAdapter channelAdapter) {
    this.channelAdapter = channelAdapter;
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  public void send(String topic, String payload) {
    send(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  public void send(String topic, byte[] payload) {
    send(topic, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  public void send(String[] topics, String payload) {
    send(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  public void send(String[] topics, byte[] payload) {
    send(topics, payload, 1);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  public void send(String topic, String payload, int qos) {
    send(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  public void send(String topic, byte[] payload, int qos) {
    send(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  public void send(String[] topics, String payload, int qos) {
    send(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  public void send(String[] topics, byte[] payload, int qos) {
    send(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  public void send(String topic, String payload, int qos, boolean retained) {
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
  public void send(String topic, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    send(topic, msg);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  public void send(String[] topics, String payload, int qos, boolean retained) {
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
  public void send(String[] topics, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setQos(qos);
    msg.setRetained(retained);
    msg.setPayload(payload);
    send(topics, msg);
  }

  /**
   * 发送
   *
   * @param topic 主题
   * @param msg   消息
   */
  public void send(String topic, MqttMessage msg) throws MqttPublishException {
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
   * @param topics 主题
   * @param msg    消息
   */
  public void send(String[] topics, MqttMessage msg) throws MqttPublishException {
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
   * 获取客户端
   */
  public IMqttClient getClient() {
    return getClient(false);
  }

  /**
   * 获取客户端
   */
  public IMqttClient getClient(boolean force) {
    IMqttClient c = this.client;
    if (c == null || !c.isConnected()) {
      if (force || isObtain()) {
        c = this.client = ReflectUtils.getFieldValue(channelAdapter
            , f -> f.getType().isAssignableFrom(IMqttClient.class));
        this.obtainTime = System.currentTimeMillis();
      }
    }
    return c;
  }

  /**
   * 是否获取客户端
   */
  protected boolean isObtain() {
    return System.currentTimeMillis() - obtainTime >= 3_000;
  }

}
