package com.benefitj.spring.vertxmqtt.publisher;


import com.benefitj.vertx.mqtt.client.VertxMqttClient;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MQTT发布客户端
 */
public interface MqttPublisher {

  Handler<AsyncResult<Integer>> IGNORE_HANDLER = (evt) -> {/* ^_^ */};

  /**
   * 发布消息
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @return 返回发布端
   */
  default MqttPublisher publish(String topic, byte[] payload) {
    return this.publish(topic, payload, MqttQoS.AT_MOST_ONCE, IGNORE_HANDLER);
  }

  /**
   * 发布消息
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @return 返回发布端
   */
  default MqttPublisher publish(String topic, String payload) {
    return this.publish(topic, payload, MqttQoS.AT_MOST_ONCE, IGNORE_HANDLER);
  }

  /**
   * 发布消息
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qosLevel 服务质量
   * @return 返回发布端
   */
  default MqttPublisher publish(String topic, byte[] payload, MqttQoS qosLevel) {
    return this.publish(topic, payload, qosLevel, IGNORE_HANDLER);
  }

  /**
   * 发布消息
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qosLevel 服务质量
   * @return 返回发布端
   */
  default MqttPublisher publish(String topic, String payload, MqttQoS qosLevel) {
    return this.publish(topic, payload, qosLevel, IGNORE_HANDLER);
  }

  /**
   * 发布消息
   *
   * @param topic              主题
   * @param payload            有效载荷
   * @param qosLevel           服务质量
   * @param publishSentHandler 发送回调处理
   * @return 返回发布端
   */
  default MqttPublisher publish(String topic, byte[] payload, MqttQoS qosLevel, Handler<AsyncResult<Integer>> publishSentHandler) {
    return this.publish(topic, Buffer.buffer(payload), qosLevel, publishSentHandler);
  }

  /**
   * 发布消息
   *
   * @param topic              主题
   * @param payload            有效载荷
   * @param qosLevel           服务质量
   * @param publishSentHandler 发送回调处理
   * @return 返回发布端
   */
  default MqttPublisher publish(String topic, String payload, MqttQoS qosLevel, Handler<AsyncResult<Integer>> publishSentHandler) {
    return this.publish(topic, Buffer.buffer(payload), qosLevel, publishSentHandler);
  }

  /**
   * 发布消息
   *
   * @param topic              主题
   * @param payload            有效载荷
   * @param qosLevel           服务质量
   * @param publishSentHandler 发送回调处理
   * @return 返回发布端
   */
  default MqttPublisher publish(String topic, Buffer payload, MqttQoS qosLevel, Handler<AsyncResult<Integer>> publishSentHandler) {
    return this.publish(topic, payload, qosLevel, false, false, publishSentHandler);
  }

  /**
   * 发布消息
   *
   * @param topic              主题
   * @param payload            有效载荷
   * @param qosLevel           服务质量
   * @param isDup              标志位用于处理重复消息，防止消息重复处理；
   * @param isRetain           标志位用于标识保留消息，确保订阅者可以接收到最新的消息。
   * @param publishSentHandler 发送回调处理
   * @return 返回发布端
   */
  default MqttPublisher publish(String topic, Buffer payload, MqttQoS qosLevel, boolean isDup, boolean isRetain, Handler<AsyncResult<Integer>> publishSentHandler) {
    getClient().publish(topic, payload, qosLevel, isDup, isRetain, publishSentHandler);
    return this;
  }

  /**
   * 获取客户端
   */
  VertxMqttClient getClient();

  static MqttPublisher create(List<VertxMqttClient> clients) {
    return create(clients, ClientDispatcher.create());
  }

  static MqttPublisher create(List<VertxMqttClient> clients, ClientDispatcher dispatcher) {
    return new Impl(clients, dispatcher);
  }

  class Impl implements MqttPublisher {

    private ClientDispatcher dispatcher;
    private final List<VertxMqttClient> clients = new CopyOnWriteArrayList<>();

    public Impl(List<VertxMqttClient> clients) {
      this(clients, ClientDispatcher.create());
    }

    public Impl(List<VertxMqttClient> clients, ClientDispatcher dispatcher) {
      this.dispatcher = dispatcher;
      this.clients.addAll(clients);
    }

    @Override
    public VertxMqttClient getClient() {
      return dispatcher.dispatch(clients);
    }
  }

}
