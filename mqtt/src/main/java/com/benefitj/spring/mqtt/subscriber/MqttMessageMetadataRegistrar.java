package com.benefitj.spring.mqtt.subscriber;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.mqtt.paho.MqttCallbackDispatcher;
import com.benefitj.mqtt.paho.PahoMqttClient;
import com.benefitj.spring.annotationprcoessor.AnnotationBeanProcessor;
import com.benefitj.spring.annotationprcoessor.AnnotationMetadata;
import com.benefitj.spring.annotationprcoessor.MetadataHandler;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.Message;

import java.util.List;

/**
 * MQTT注册器
 */
public class MqttMessageMetadataRegistrar extends AnnotationBeanProcessor implements MetadataHandler {

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

  private MqttMessageConverter messageConverter = new DefaultPahoMessageConverter();

  public MqttMessageMetadataRegistrar(MqttConnectOptions options) {
    this.options = options;
    this.setAnnotationType(MqttMessageListener.class);
    this.setMetadataHandler(this);
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      MqttMessageListener listener = (MqttMessageListener) metadata.getAnnotation();
      if (listener.singleClient()) {
        // 单独的客户端
        String id = IdUtils.nextLowerLetterId(listener.clientIdPrefix(), null, 16);
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

  public MqttCallbackDispatcher getDispatcher() {
    return dispatcher;
  }

  public void setDispatcher(MqttCallbackDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }
}
