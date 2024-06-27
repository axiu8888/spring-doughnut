package com.benefitj.spring.mqtt.publisher;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.mqtt.IMqttPublisher;
import com.benefitj.mqtt.paho.v3.PahoMqttV3Client;
import com.benefitj.spring.mqtt.MqttOptions;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * MQTT发送端
 */
public class MqttPublisherImpl implements IMqttPublisher, InitializingBean, DisposableBean {

  /**
   * 连接配置
   */
  private MqttOptions options;
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
  private List<PahoMqttV3Client> clients;
  /**
   * 调度器
   */
  private EventLoop executor = EventLoop.io();

  private final AtomicInteger dispatcher = new AtomicInteger();

  public MqttPublisherImpl(MqttOptions options) {
    this(options, "mqtt-publisher-");
  }

  public MqttPublisherImpl(MqttOptions options, String prefix) {
    this(options, prefix, 1);
  }

  public MqttPublisherImpl(MqttOptions options, String prefix, int count) {
    this.options = options;
    this.prefix = prefix;
    this.count = count;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    MqttConnectOptions mcOpts = options.toMqttConnectOptions();
    String prefix = getPrefix() + IdUtils.uuid(0, 10) + "-";
    int count = getCount();
    List<PahoMqttV3Client> clients = new ArrayList<>(count);
    for (int i = 1; i <= count; i++) {
      PahoMqttV3Client client = new PahoMqttV3Client(mcOpts, prefix + i);
      client.setAutoConnectTimer(timer -> timer.setAutoConnect(options.isAutoReconnect(), Duration.ofSeconds(options.getReconnectDelay())));
      EventLoop.asyncIO(client::connect);
      clients.add(client);
    }
    this.setClients(Collections.unmodifiableList(clients));
  }

  @Override
  public void destroy() throws Exception {
    getClients().forEach(c -> CatchUtils.tryThrow(c::disconnectForcibly, (Consumer<Throwable>) Throwable::printStackTrace));
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
    PahoMqttV3Client client = this.clients.get(index % this.clients.size());
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

  public List<PahoMqttV3Client> getClients() {
    return clients;
  }

  public void setClients(List<PahoMqttV3Client> clients) {
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
