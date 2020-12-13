package com.benefitj.spring.mqtt;

import com.benefitj.core.IdUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * MQTT发送端
 */
public class MqttPublisher implements InitializingBean, DisposableBean {

  /**
   * 客户端
   */
  private MqttPahoClientFactory clientFactory;
  private String prefix;

  private IMqttClient client;
  private Executor executor;

  private volatile long tryReconnectTime = System.currentTimeMillis();

  public MqttPublisher(MqttPahoClientFactory clientFactory, String prefix) {
    this.clientFactory = clientFactory;
    this.prefix = prefix;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      IMqttClient c = getRawClient();
      if (!c.isConnected()) {
        c.connect(clientFactory.getConnectionOptions());
      }
      this.executor = Executors.newSingleThreadExecutor();
    } catch (MqttException ignore) { /* ~ */}
  }

  @Override
  public void destroy() throws Exception {
    try {
      getRawClient().disconnect();
    } catch (MqttException ignore) { /* ~ */}
  }


  private IMqttClient getRawClient() {
    IMqttClient c = this.client;
    if (c == null) {
      synchronized (this) {
        if ((c = this.client) != null) {
          return c;
        }
        try {
          String clientId = IdUtils.nextLowerLetterId(prefix + "sender-", null, 6);
          MqttConnectOptions options = clientFactory.getConnectionOptions();
          c = clientFactory.getClientInstance(options.getServerURIs()[0], clientId);
          this.client = c;
        } catch (MqttException e) {
          throw new MqttPublishException(e);
        }
      }
    }
    return c;
  }

  public void setClient(IMqttClient client) {
    this.client = client;
  }

  /**
   * 获取客户端
   */
  public IMqttClient getClient() {
    IMqttClient c = getRawClient();
    if (!c.isConnected()) {
      long now = System.currentTimeMillis();
      if (now - tryReconnectTime <= 3000) {
        return c;
      }
      synchronized (this) {
        if (now - tryReconnectTime > 3000) {
          try {
            //c.connect(clientFactory.getConnectionOptions());
            c.reconnect();
          } catch (MqttException ignore) { /* ~ */ } finally {
            this.tryReconnectTime = now;
          }
        }
      }
    }
    return c;
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
  public void sendAsync(String topic, String payload) {
    sendAsync(topic, payload, 1);
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
   * @param topic   主题
   * @param payload 有效载荷
   */
  public void sendAsync(String topic, byte[] payload) {
    sendAsync(topic, payload, 1);
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
  public void sendAsync(String[] topics, String payload) {
    sendAsync(topics, payload, 1);
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
   * @param topics  主题
   * @param payload 有效载荷
   */
  public void sendAsync(String[] topics, byte[] payload) {
    sendAsync(topics, payload, 1);
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
  public void sendAsync(String topic, String payload, int qos) {
    sendAsync(topic, payload, qos, false);
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
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  public void sendAsync(String topic, byte[] payload, int qos) {
    sendAsync(topic, payload, qos, false);
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
  public void sendAsync(String[] topics, String payload, int qos) {
    sendAsync(topics, payload, qos, false);
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
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  public void sendAsync(String[] topics, byte[] payload, int qos) {
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
  public void sendAsync(String topic, String payload, int qos, boolean retained) {
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
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  public void sendAsync(String topic, byte[] payload, int qos, boolean retained) {
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
  public void sendAsync(String[] topics, String payload, int qos, boolean retained) {
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
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  public void sendAsync(String[] topics, byte[] payload, int qos, boolean retained) {
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
   * @param topic 主题
   * @param msg   消息
   */
  public void sendAsync(String topic, MqttMessage msg) {
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
   * 发送
   *
   * @param topics 主题
   * @param msg    消息
   */
  public void sendAsync(String[] topics, MqttMessage msg) {
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

  public Executor getExecutor() {
    return executor;
  }

  /**
   * 异步执行
   *
   * @param consumer
   */
  public void execute(Consumer<IMqttClient> consumer) {
    getExecutor().execute(() -> consumer.accept(getClient()));
  }

}
