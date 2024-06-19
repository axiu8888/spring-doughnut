package com.benefitj.spring.vertxmqtt.subscriber;

import com.benefitj.spring.listener.AppStateListener;
import com.benefitj.spring.listener.AppStateListenerWrapper;
import com.benefitj.spring.listener.EnableAppStateListener;
import com.benefitj.spring.vertxmqtt.MqttClientOptions;
import com.benefitj.spring.vertxmqtt.VertxMqttClientFactory;
import com.benefitj.vertx.VertxHolder;
import com.benefitj.vertx.mqtt.client.VertxMqttClient;
import com.benefitj.vertx.mqtt.client.VertxMqttMessageDispatcher;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT 消息订阅
 */
@EnableAppStateListener
@Configuration
public class MqttSubscriberConfiguration {

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
  @ConfigurationProperties(prefix = "spring.mqtt.subscribe")
  @ConditionalOnMissingBean(name = "mqttSubscribeOptions")
  @Bean("mqttSubscribeOptions")
  public MqttClientOptions mqttSubscriberOptions() {
    return new MqttClientOptions();
  }

  /**
   * Mqtt客户端工厂
   */
  @ConditionalOnMissingBean
  @Bean
  public VertxMqttClientFactory vertxClientFactory() {
    return VertxMqttClientFactory.newFactory();
  }

  /**
   * MQTT订阅客户端
   */
  @ConditionalOnMissingBean(name = "mqttSubscriberClient")
  @Bean("mqttSubscriberClient")
  public VertxMqttClient mqttSubscriberClient(VertxMqttClientFactory factory,
                                              @Qualifier("mqttSubscribeOptions") MqttClientOptions options,
                                              @Qualifier("mqttSubscribeDispatcher") VertxMqttMessageDispatcher dispatcher) {
    VertxMqttClient client = factory.create(options);
    client.addHandler(dispatcher);
    return client;
  }

  /**
   * 消息分发器
   */
  @ConditionalOnMissingBean(name = "mqttSubscribeDispatcher")
  @Bean(name = "mqttSubscribeDispatcher")
  public VertxMqttMessageDispatcher mqttSubscriberDispatcher() {
    return new VertxMqttMessageDispatcher();
  }

  /**
   * MQTT订阅开关
   */
  @ConditionalOnMissingBean(name = "mqttSubscribeSwitcher")
  @Bean("mqttSubscribeSwitcher")
  public AppStateListener mqttSubscribeSwitcher(@Qualifier("vertx") Vertx vertx,
                                                @Qualifier("mqttSubscriberClient") VertxMqttClient client) {
    return new AppStateListenerWrapper(
        // 部署
        e -> vertx.deployVerticle(client),
        // 停止
        e -> client.stop()
    );
  }

  /**
   * 监听注册器
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttSubscriberRegistrar mqttSubscriberRegistrar(@Qualifier("mqttSubscribeDispatcher") VertxMqttMessageDispatcher dispatcher) {
    MqttSubscriberRegistrar registrar = new MqttSubscriberRegistrar();
    registrar.setDispatcher(dispatcher);
    return registrar;
  }

}
