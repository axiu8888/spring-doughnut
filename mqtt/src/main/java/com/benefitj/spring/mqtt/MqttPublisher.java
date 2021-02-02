package com.benefitj.spring.mqtt;

import com.benefitj.core.IdUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
  private String prefix = "mqtt-publisher-";
  /**
   * 客户端
   */
  private SingleMqttClient client;

  public MqttPublisher(MqttPahoClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  public MqttPublisher(MqttPahoClientFactory clientFactory, String prefix) {
    this.clientFactory = clientFactory;
    this.prefix = prefix;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    String clientId = IdUtils.nextLowerLetterId(getPrefix(), null, 6);
    // 设置自动连接
    this.clientFactory.getConnectionOptions().setAutomaticReconnect(true);
    this.client = new SingleMqttClient(this.clientFactory, clientId);
    this.client.setExecutor(Executors.newSingleThreadScheduledExecutor());
  }

  @Override
  public void destroy() throws Exception {
    this.client.disconnect();
  }

  /**
   * 获取客户端
   */
  @Override
  public IMqttClient getClient() {
    return this.client;
  }

  @Override
  public Executor getExecutor() {
    return this.client.getExecutor();
  }

  /**
   * 异步执行
   *
   * @param consumer
   */
  public void execute(Consumer<IMqttClient> consumer) {
    getExecutor().execute(() -> consumer.accept(getClient()));
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

}
