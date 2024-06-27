package com.benefitj.spring.mqtt.subscriber;

import com.benefitj.core.IdUtils;
import com.benefitj.mqtt.paho.v3.PahoMqttV3Client;
import com.benefitj.mqtt.paho.v3.PahoMqttV3Dispatcher;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.mqtt.MqttOptions;
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

import java.time.Duration;

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
  public PahoMqttV3Client mqttSubscriberClient(MqttOptions options,
                                               @Qualifier("mqttSubscribeDispatcher") PahoMqttV3Dispatcher dispatcher) {
    String prefix = StringUtils.getIfBlank(clientIdPrefix, () -> appName + "-publisher-");
    MqttConnectOptions mcOpts = options.toMqttConnectOptions();
    PahoMqttV3Client client = new PahoMqttV3Client(mcOpts, prefix + IdUtils.uuid(0, 10));
    client.setCallback(dispatcher);
    client.setAutoConnectTimer(timer -> timer.setAutoConnect(options.isAutoReconnect(), Duration.ofSeconds(options.getReconnectDelay())));
    return client;
  }

  /**
   * 消息分发器
   */
  @ConditionalOnMissingBean(name = "mqttSubscribeDispatcher")
  @Bean("mqttSubscribeDispatcher")
  public PahoMqttV3Dispatcher mqttSubscribeDispatcher() {
    return new PahoMqttV3Dispatcher();
  }

  /**
   * 监听器注册
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttMessageMetadataRegistrar mqttMessageListenerRegistrar(MqttOptions options,
                                                                   MqttMessageConverter messageConverter,
                                                                   @Qualifier("mqttSubscriber") IMqttClient client,
                                                                   @Qualifier("mqttSubscribeDispatcher") PahoMqttV3Dispatcher dispatcher) {
    MqttMessageMetadataRegistrar registrar = new MqttMessageMetadataRegistrar(options);
    registrar.setClient(client);
    registrar.setDispatcher(dispatcher);
    registrar.setMessageConverter(messageConverter);
    registrar.setPrefix(StringUtils.getIfBlank(clientIdPrefix, () -> appName + "-publisher-"));
    return registrar;
  }

}
