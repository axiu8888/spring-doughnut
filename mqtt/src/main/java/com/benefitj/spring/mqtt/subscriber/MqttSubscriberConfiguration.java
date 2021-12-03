package com.benefitj.spring.mqtt.subscriber;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.mqtt.paho.MqttCallbackDispatcher;
import com.benefitj.mqtt.paho.PahoMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;

/**
 * MQTT消息订阅
 */
@Configuration
public class MqttSubscriberConfiguration {

  @Value("#{@environment['spring.mqtt.subscriber.client-id-prefix'] ?: 'mqtt-subscriber-'}")
  private String clientIdPrefix;

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
  public PahoMqttClient mqttSubscriberClient(MqttConnectOptions options,
                                             @Qualifier("mqttSubscribeDispatcher") MqttCallbackDispatcher dispatcher) {
    String clientId = IdUtils.nextId(clientIdPrefix, null, 10);
    PahoMqttClient client = new PahoMqttClient(options, clientId);
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
  public MqttMessageMetadataRegistrar mqttMessageListenerRegistrar(MqttConnectOptions options,
                                                                   MqttMessageConverter messageConverter,
                                                                   @Qualifier("mqttSubscriber") IMqttClient client,
                                                                   @Qualifier("mqttSubscribeDispatcher") MqttCallbackDispatcher dispatcher) {
    MqttMessageMetadataRegistrar registrar = new MqttMessageMetadataRegistrar(options);
    registrar.setClient(client);
    registrar.setDispatcher(dispatcher);
    registrar.setMessageConverter(messageConverter);
    return registrar;
  }

}
