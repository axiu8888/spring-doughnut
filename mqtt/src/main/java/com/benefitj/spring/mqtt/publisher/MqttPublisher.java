package com.benefitj.spring.mqtt.publisher;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.spring.mqtt.SimpleMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * MQTT发送端
 */
public class MqttPublisher implements IMqttSender, InitializingBean, DisposableBean {

  /**
   * 客户端
   */
  private MqttPahoClientFactory clientFactory;
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
  private List<SimpleMqttClient> clients;
  /**
   * 调度器
   */
  private ExecutorService executor = EventLoop.io();

  private final AtomicInteger dispatcher = new AtomicInteger();

  public MqttPublisher(MqttPahoClientFactory clientFactory) {
    this(clientFactory, "mqtt-publisher-");
  }

  public MqttPublisher(MqttPahoClientFactory clientFactory, String prefix) {
    this(clientFactory, prefix, 1);
  }

  public MqttPublisher(MqttPahoClientFactory clientFactory, String prefix, int count) {
    this.clientFactory = clientFactory;
    this.prefix = prefix;
    this.count = count;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    String id = IdUtils.nextLowerLetterId(getPrefix(), null, 4);
    int count = this.getCount();
    // 设置自动连接
    MqttPahoClientFactory factory = this.clientFactory;
    factory.getConnectionOptions().setAutomaticReconnect(true);

    EventLoop executor = EventLoop.newSingle(false);
    List<SimpleMqttClient> clients = new ArrayList<>(count);
    for (int i = 1; i <= count; i++) {
      SimpleMqttClient client = new SimpleMqttClient(factory, id + i);
      client.setExecutor(executor);
      clients.add(client);
    }
    this.setClients(Collections.unmodifiableList(clients));
  }

  @Override
  public void destroy() throws Exception {
    for (SimpleMqttClient client : getClients()) {
      client.disconnect();
    }
  }

  /**
   * 获取客户端
   */
  @Override
  public IMqttClient getClient() {
    int index = dispatcher.incrementAndGet();
    SimpleMqttClient client = this.clients.get(index % this.clients.size());
    if (index > 10000) {
      dispatcher.set(this.clients.size());
    }
    return client;
  }

  @Override
  public Executor getExecutor() {
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

  public List<SimpleMqttClient> getClients() {
    return clients;
  }

  public void setClients(List<SimpleMqttClient> clients) {
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
