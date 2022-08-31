package com.benefitj.spring.mqtt.subscriber;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.mqtt.paho.MqttCallbackDispatcher;
import com.benefitj.mqtt.paho.PahoMqttClient;
import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.AnnotationResolverImpl;
import com.benefitj.spring.annotation.MetadataHandler;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MQTT注册器
 */
public class MqttMessageMetadataRegistrar extends AnnotationBeanProcessor implements MetadataHandler, DisposableBean {

  /**
   * 代理
   */
  private MqttConnectOptions options;
  /**
   * 默认的订阅客户端
   */
  private IMqttClient client;
  /**
   * 消息分发器
   */
  private MqttCallbackDispatcher dispatcher;
  /**
   * 客户端ID前缀
   */
  private String prefix;

  private MqttMessageConverter messageConverter = new DefaultPahoMessageConverter();

  private final List<PahoMqttClient> singleClients = new CopyOnWriteArrayList<>();

  public MqttMessageMetadataRegistrar(MqttConnectOptions options) {
    this.options = options;
    this.setMetadataHandler(this);
    this.setResolver(new AnnotationResolverImpl(MqttMessageListener.class));
  }

  @Override
  public void destroy() throws Exception {
    singleClients.forEach(c -> CatchUtils.tryThrow(c::disconnect, Exception::printStackTrace));
    CatchUtils.tryThrow(getClient()::disconnect, Exception::printStackTrace);
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      MqttMessageListener listener = metadata.getFirstAnnotation(MqttMessageListener.class);
      if (listener.singleClient()) {
        // 单独的客户端
        String prefix = StringUtils.isNotBlank(listener.clientIdPrefix()) ? listener.clientIdPrefix() : getPrefix();
        String id = IdUtils.nextLowerLetterId(prefix, null, 12);
        PahoMqttClient client = new PahoMqttClient(getOptions(), id);
        client.setExecutor(EventLoop.newSingle(false));
        // 自动重连
        client.setAutoReconnect(true);
        // 重新连接的间隔
        client.setDelay(5000);
        // 消息分发器，自动重连等
        MqttCallbackDispatcher dispatcher = new MqttCallbackDispatcher();
        client.setCallback(dispatcher);
        // 订阅
        subscribe(metadata, client, dispatcher, listener);
        singleClients.add(client);
      } else {
        subscribe(metadata, getClient(), getDispatcher(), listener);
      }
    }
  }

  protected void subscribe(AnnotationMetadata metadata,
                           IMqttClient client,
                           MqttCallbackDispatcher dispatcher,
                           MqttMessageListener listener) {
    final SimpleMethodInvoker invoker = new SimpleMethodInvoker(metadata.getBean(), metadata.getMethod());
    dispatcher.subscribe(listener.topics(), (topic, msg) -> {
      Message<?> message = getMessageConverter().toMessage(topic, msg);
      invoker.invoke(topic, msg.getPayload(), msg, message, message.getHeaders());
    });
    try {
      if (!client.isConnected()) {
        // 连接客户端
        client.connect();
      }
    } catch (MqttException ignore) {/* ~  */ }
  }


  public MqttConnectOptions getOptions() {
    return options;
  }

  public void setOptions(MqttConnectOptions options) {
    this.options = options;
  }

  public MqttMessageConverter getMessageConverter() {
    return messageConverter;
  }

  public void setMessageConverter(MqttMessageConverter messageConverter) {
    this.messageConverter = messageConverter;
  }

  public IMqttClient getClient() {
    return client;
  }

  public void setClient(IMqttClient client) {
    this.client = client;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public MqttCallbackDispatcher getDispatcher() {
    return dispatcher;
  }

  public void setDispatcher(MqttCallbackDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

}
