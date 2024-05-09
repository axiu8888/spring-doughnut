package com.benefitj.spring.vertxmqtt.publisher;

import com.benefit.vertx.VertxHolder;
import com.benefit.vertx.mqtt.client.VertxMqttClient;
import com.benefitj.spring.listener.EnableAppStateListener;
import com.benefitj.spring.vertxmqtt.MqttClientOptions;
import com.benefitj.spring.vertxmqtt.VertxMqttClientFactory;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * MQTT 消息发布
 */
@EnableAppStateListener
@Configuration
public class MqttPublisherConfiguration {

  /**
   * MQTT客户端数量
   */
  @Value("#{@environment['spring.mqtt.publish.count'] ?: 1}")
  Integer count;

  /**
   * vertx
   */
  @ConditionalOnMissingBean(name = "vertx")
  @Bean("vertx")
  public Vertx vertx() {
    return VertxHolder.getVertx();
  }

  /**
   * 配置
   */
  @ConfigurationProperties(prefix = "spring.mqtt.publish")
  @ConditionalOnMissingBean(name = "mqttPublishOptions")
  @Bean("mqttPublishOptions")
  public MqttClientOptions mqttPublisherOptions() {
    return new MqttClientOptions();
  }

  /**
   * MQTT发布客户端
   */
  @ConditionalOnMissingBean(name = "mqttPublisher")
  @Bean("mqttPublisher")
  public MqttPublisher mqttPublisher(@Qualifier("vertx") Vertx vertx,
                                     @Qualifier("mqttPublishOptions") MqttClientOptions options) {
    VertxMqttClientFactory<VertxMqttClient> factory = VertxMqttClientFactory.newFactory(VertxMqttClient.class);
    List<VertxMqttClient> clients = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      clients.add(factory.create(options));
    }
    clients.forEach(vertx::deployVerticle);
    return MqttPublisher.create(clients);
  }

}
