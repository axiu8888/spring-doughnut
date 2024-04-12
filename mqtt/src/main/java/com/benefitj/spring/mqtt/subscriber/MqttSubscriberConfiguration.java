package com.benefitj.spring.mqtt.subscriber;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.mqtt.paho.MqttCallbackDispatcher;
import com.benefitj.mqtt.paho.PahoMqttClient;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import org.apache.commons.lang3.StringUtils;
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
@EnableSpringCtxInit
@Configuration
public class MqttSubscriberConfiguration {

  @Value("#{@environment['spring.mqtt.subscriber.client-id-prefix'] ?: null}")
  private String clientIdPrefix;
  /**
   * 程序名称
   */
  @Value("#{@environment['spring.application.name'] ?: 'mqtt'}")
  private String appName;

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
    String prefix = StringUtils.isNotBlank(clientIdPrefix) ? clientIdPrefix : appName + "-publisher-";
    PahoMqttClient client = new PahoMqttClient(options, IdUtils.nextId(prefix, null, 10));
    client.setCallback(dispatcher);
    client.setAutoReconnect(true);
    client.setExecutor(EventLoop.newSingle(false));
    client.getExecutor().execute(() -> {});
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
    registrar.setPrefix(StringUtils.isNotBlank(clientIdPrefix) ? clientIdPrefix : appName + "-publisher-");
    return registrar;
  }

}
