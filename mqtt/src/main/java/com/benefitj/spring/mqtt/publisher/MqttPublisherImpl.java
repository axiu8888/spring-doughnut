package com.benefitj.spring.mqtt.publisher;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.mqtt.paho.PahoMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * MQTT发送端
 */
public class MqttPublisherImpl implements IMqttPublisher, InitializingBean, DisposableBean {

  private MqttConnectOptions options;
  /**
   * 前缀
   */
  private String prefix;
  /**
   * 客户端数量
   */
  private int count;
  /**
   * 客户端
   */
  private List<PahoMqttClient> clients;
  /**
   * 调度器
   */
  private EventLoop executor = EventLoop.io();

  private final AtomicInteger dispatcher = new AtomicInteger();

  public MqttPublisherImpl(MqttConnectOptions options) {
    this(options, "mqtt-publisher-");
  }

  public MqttPublisherImpl(MqttConnectOptions options, String prefix) {
    this(options, prefix, 1);
  }

  public MqttPublisherImpl(MqttConnectOptions options, String prefix, int count) {
    this.options = options;
    this.prefix = prefix;
    this.count = count;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    String id = IdUtils.nextLowerLetterId(getPrefix(), null, 6);
    int count = getCount();
    List<PahoMqttClient> clients = new ArrayList<>(count);
    for (int i = 1; i <= count; i++) {
      PahoMqttClient client = new PahoMqttClient(options, id + "-" + i);
      client.setExecutor(getExecutor());
      clients.add(client);
    }
    this.setClients(Collections.unmodifiableList(clients));
  }

  @Override
  public void destroy() throws Exception {
    getClients().forEach(c -> CatchUtils.tryThrow(c::disconnectForcibly, (Consumer<Exception>) Exception::printStackTrace));
  }

  @Override
  public void publish(String topic, MqttMessage msg) throws MqttPublishException {
    try {
      getClient().publish(topic, msg);
    } catch (MqttException e) {
      throw new MqttPublishException(e);
    }
  }

  @Override
  public void publishAsync(String topic, MqttMessage msg) {
    getExecutor().execute(() -> publish(topic, msg));
  }

  /**
   * 获取客户端
   */
  public IMqttClient getClient() {
    int index = dispatcher.incrementAndGet();
    PahoMqttClient client = this.clients.get(index % this.clients.size());
    if (index > 10000) {
      dispatcher.set(this.clients.size());
    }
    return client;
  }

  public ScheduledExecutorService getExecutor() {
    return this.executor;
  }

  /**
   * 异步执行
   *
   * @param consumer
   */
  public void execute(Consumer<IMqttClient> consumer) {
    getExecutor().execute(() -> consumer.accept(getClient()));
  }

  public List<PahoMqttClient> getClients() {
    return clients;
  }

  public void setClients(List<PahoMqttClient> clients) {
    this.clients = clients;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

}
