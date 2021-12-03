package com.benefitj.spring.mqtt.publisher;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.mqtt.paho.PahoMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
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
public class MqttPublisher implements IMqttPublisher, InitializingBean, DisposableBean {

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

  public MqttPublisher(MqttConnectOptions options) {
    this(options, "mqtt-publisher-");
  }

  public MqttPublisher(MqttConnectOptions options, String prefix) {
    this(options, prefix, 1);
  }

  public MqttPublisher(MqttConnectOptions options, String prefix, int count) {
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
    for (PahoMqttClient client : getClients()) {
      client.disconnect();
    }
  }

  /**
   * 获取客户端
   */
  @Override
  public IMqttClient getClient() {
    int index = dispatcher.incrementAndGet();
    PahoMqttClient client = this.clients.get(index % this.clients.size());
    if (index > 10000) {
      dispatcher.set(this.clients.size());
    }
    return client;
  }

  @Override
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
