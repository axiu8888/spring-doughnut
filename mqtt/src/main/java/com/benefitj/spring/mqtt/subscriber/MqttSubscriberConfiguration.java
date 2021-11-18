package com.benefitj.spring.mqtt.subscriber;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.spring.mqtt.MqttCallbackDispatcher;
import com.benefitj.spring.mqtt.MqttOptionsProperty;
import com.benefitj.spring.mqtt.SimpleMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;

/**
 * MQTT消息订阅
 */
@Configuration
public class MqttSubscriberConfiguration {

  /**
   * 消息转换
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttMessageConverter mqttMessageConverter() {
    DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
    converter.setPayloadAsBytes(true);
    return converter;
  }

  /**
   * 默认的订阅客户端
   */
  @ConditionalOnMissingBean(name = "mqttSubscriber")
  @Bean("mqttSubscriber")
  public SimpleMqttClient mqttSubscriberClient(MqttPahoClientFactory clientFactory,
                                               @Qualifier("mqttSubscribeDispatcher") MqttCallbackDispatcher dispatcher) {
    String clientId = IdUtils.nextId("mqtt-subscriber-", null, 10);
    SimpleMqttClient client = new SimpleMqttClient(clientFactory, clientId);
    client.setCallback(dispatcher);
    client.setAutoReconnect(true);
    client.setExecutor(EventLoop.newSingle(false));
    return client;
  }

  /**
   * 消息分发器
   */
  @ConditionalOnMissingBean(name = "mqttSubscribeDispatcher")
  @Bean("mqttSubscribeDispatcher")
  public MqttCallbackDispatcher mqttSubscribeDispatcher() {
    return new MqttCallbackDispatcher();
  }

  /**
   * 监听器注册
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttMessageMetadataRegistrar mqttMessageListenerRegistrar(MqttOptionsProperty property,
                                                                   MqttPahoClientFactory clientFactory,
                                                                   MqttMessageConverter messageConverter,
                                                                   @Qualifier("mqttSubscriber") IMqttClient client,
                                                                   @Qualifier("mqttSubscribeDispatcher") MqttCallbackDispatcher dispatcher) {
    MqttMessageMetadataRegistrar registrar = new MqttMessageMetadataRegistrar(clientFactory);
    registrar.setProperty(property);
    registrar.setClient(client);
    registrar.setDispatcher(dispatcher);
    registrar.setMessageConverter(messageConverter);
    return registrar;
  }

}
