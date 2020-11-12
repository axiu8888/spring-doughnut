package com.benefitj.spring.mqtt;

import com.benefitj.core.IdUtils;
import com.benefitj.spring.eventbus.EnableAutoEventBusPoster;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MQTT服务配置
 */
@Lazy
@EnableAutoEventBusPoster
@EnableConfigurationProperties
@Configuration
public class MqttConfiguration {

  public static final char[] HEX = "0123456789abcdef".toCharArray();

  /**
   * MQTT配置
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttOptionsProperty mqttOptionsProperty() {
    return new MqttOptionsProperty();
  }

  /**
   * MQTT连接器选项
   */
  @Bean
  public MqttConnectOptions mqttConnectOptions(MqttOptionsProperty property) {
    MqttConnectOptions options = new MqttConnectOptions();
    // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，
    // 这里设置为true表示每次连接到服务器都以新的身份连接
    options.setCleanSession(property.getCleanSession());
    // 设置连接的用户名
    options.setUserName(property.getUsername());
    // 设置连接的密码
    options.setPassword(property.getPassword().toCharArray());
    options.setServerURIs(StringUtils.split(property.getServerURIs(), ","));
    // 设置超时时间 单位为秒
    options.setConnectionTimeout(property.getConnectionTimeout());
    // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
    options.setKeepAliveInterval(property.getKeepalive());
    // 设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息。
    //options.setWill("willTopic", WILL_DATA, 2, false);
    // 自动重连
    options.setAutomaticReconnect(property.getAutomaticReconnect());
    return options;
  }

  /**
   * MQTT客户端
   */
  @Bean
  public MqttPahoClientFactory mqttClientFactory(MqttConnectOptions options) {
    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    factory.setConnectionOptions(options);
    return factory;
  }

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
   * MQTT消息订阅绑定（消费者）
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttPahoMessageDrivenChannelAdapter channelAdapter(MqttPahoClientFactory clientFactory,
                                                            MqttOptionsProperty property,
                                                            MqttMessageConverter converter,
                                                            MqttMessageSubscriber subscriber) {
    String clientId = getClientId(property);
    MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, clientFactory);
    adapter.setCompletionTimeout(property.getCompletionTimeout());
    adapter.setRecoveryInterval(property.getRecoveryInterval());
    adapter.setConverter(converter);
    // 服务质量
    adapter.setQos(property.getQos());
    // 设置订阅通道
    DirectChannel mqttOutputChannel = new DirectChannel();
    mqttOutputChannel.setBeanName("mqttOutputChannel");
    mqttOutputChannel.subscribe(subscriber);
    adapter.setOutputChannel(mqttOutputChannel);
    // 添加 topic
    addTopics(adapter, property);

    return adapter;
  }

  /**
   * 添加topic
   */
  protected void addTopics(MqttPahoMessageDrivenChannelAdapter adapter, MqttOptionsProperty property) {
    //Collections.addAll(topics, property.getPublishTopics().split(","));
    // 订阅消息
    String subscribeTopics = property.getSubscribeTopics();
    if (StringUtils.isNotBlank(subscribeTopics)) {
      List<String> topics = new ArrayList<>();
      Collections.addAll(topics, subscribeTopics.split(","));
      topics.stream().filter(StringUtils::isNotBlank)
          .forEach(topic -> adapter.addTopic(topic, property.getQos()));
    } else {
      // 默认的topic
      adapter.addTopic("/empty", 0);
    }
  }

  /**
   * 获取客户端ID
   *
   * @param property 属性配置
   * @return 返回客户端ID
   */
  protected String getClientId(MqttOptionsProperty property) {
    String clientId = property.getClientId();
    int clientLen = StringUtils.isNotBlank(clientId) ? clientId.length() : 0;
    return clientLen >= 32 ? clientId : clientId + IdUtils.nextId(HEX, 32 - clientLen);
  }

  /**
   * MQTT消息订阅者
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttMessageSubscriber messageSubscriber() {
    return MqttMessageSubscriber.DISCARD_SUBSCRIBER;
  }

  /**
   * 消息发布的客户端
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttPublisher mqttPublisher(MqttPahoMessageDrivenChannelAdapter channelAdapter) {
    return new MqttPublisher(channelAdapter);
  }

  /**
   * 蓝牙消息发送
   */
  @ConditionalOnMissingBean
  @Bean
  public MessagePublisherAdapter messagePublisherAdapter(MqttPublisher mqttPublisher) {
    return new MessagePublisherAdapter(mqttPublisher);
  }

}
