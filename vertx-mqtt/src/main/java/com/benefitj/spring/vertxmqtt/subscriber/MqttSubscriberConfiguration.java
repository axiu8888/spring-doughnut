package com.benefitj.spring.vertxmqtt.subscriber;

import com.benefit.vertx.VertxHolder;
import com.benefit.vertx.mqtt.client.VertxMqttClient;
import com.benefit.vertx.mqtt.client.VertxMqttMessageDispatcher;
import com.benefitj.spring.listener.AppStateListener;
import com.benefitj.spring.listener.AppStateListenerWrapper;
import com.benefitj.spring.vertxmqtt.MqttClientProperty;
import com.benefitj.spring.vertxmqtt.VertxClientFactory;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT 消息订阅
 */
@Configuration
public class MqttSubscriberConfiguration {

  /**
   * vertx
   */
  @ConditionalOnMissingBean(name = "vertx")
  @Bean("vertx")
  public Vertx vertx() {
    return VertxHolder.get();
  }

  /**
   * 配置
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttClientProperty mqttClientProperty() {
    return new MqttClientProperty();
  }

  /**
   * Mqtt客户端工厂
   */
  @ConditionalOnMissingBean
  @Bean
  public VertxClientFactory vertxClientFactory() {
    return VertxClientFactory.newFactory();
  }

  /**
   * MQTT订阅客户端
   */
  @ConditionalOnMissingBean(name = "mqttSubscriberClient")
  @Bean("mqttSubscriberClient")
  public VertxMqttClient mqttSubscriberClient(VertxClientFactory factory,
                                              MqttClientProperty property,
                                              @Qualifier("mqttSubscriberDispatcher") VertxMqttMessageDispatcher dispatcher) {
    VertxMqttClient client = factory.create(property);
    client.addHandler(dispatcher);
    return client;
  }

  /**
   * 消息分发器
   */
  @ConditionalOnMissingBean(name = "mqttSubscriberDispatcher")
  @Bean(name = "mqttSubscriberDispatcher")
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
  public MqttSubscriberRegistrar mqttSubscriberRegistrar(@Qualifier("mqttSubscriberDispatcher") VertxMqttMessageDispatcher dispatcher) {
    MqttSubscriberRegistrar registrar = new MqttSubscriberRegistrar();
    registrar.setDispatcher(dispatcher);
    return registrar;
  }

}
