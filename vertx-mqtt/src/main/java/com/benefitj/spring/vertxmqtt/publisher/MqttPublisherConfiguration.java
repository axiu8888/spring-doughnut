package com.benefitj.spring.vertxmqtt.publisher;

import com.benefitj.mqtt.vertx.VertxHolder;
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
 * MQTT 消息发布
 */
@Configuration
public class MqttPublisherConfiguration {

  /**
   * vertx
   */
  @ConditionalOnMissingBean(name = "vertx")
  @Bean("vertx")
  public Vertx vertx() {
    return VertxHolder.getInstance();
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
   * MQTT发布客户端
   */
  @ConditionalOnMissingBean(name = "mqttPublisher")
  @Bean("mqttPublisher")
  public MqttPublisher mqttPublisher(MqttClientProperty property) {
    VertxClientFactory factory = VertxClientFactory.newFactory(MqttPublisher.class);
    return (MqttPublisher) factory.create(property);
  }

  /**
   * MQTT订阅开关
   */
  @ConditionalOnMissingBean(name = "mqttSubscribeSwitcher")
  @Bean("mqttSubscribeSwitcher")
  public AppStateListener mqttSubscribeSwitcher(@Qualifier("vertx") Vertx vertx,
                                                @Qualifier("mqttPublisher") MqttPublisher publisher) {
    return new AppStateListenerWrapper(e -> vertx.deployVerticle(publisher), e -> publisher.stop());
  }

}
