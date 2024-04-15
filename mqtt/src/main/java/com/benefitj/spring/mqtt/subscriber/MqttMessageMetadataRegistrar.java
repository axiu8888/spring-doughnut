package com.benefitj.spring.mqtt.subscriber;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.mqtt.MqttMessageSubscriber;
import com.benefitj.mqtt.paho.v3.PahoMqttV3Client;
import com.benefitj.mqtt.paho.v3.PahoMqttV3Dispatcher;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.AnnotationResolverImpl;
import com.benefitj.spring.annotation.MetadataHandler;
import com.benefitj.spring.ctx.SpringCtxHolder;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.Message;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * MQTT注册器
 */
public class MqttMessageMetadataRegistrar extends AnnotationBeanProcessor implements MetadataHandler, DisposableBean {

  static final Logger log = LoggerFactory.getLogger(MqttMessageMetadataRegistrar.class);

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
  private PahoMqttV3Dispatcher dispatcher;
  /**
   * 客户端ID前缀
   */
  private String prefix;

  private MqttMessageConverter messageConverter = new DefaultPahoMessageConverter();

  private final List<PahoMqttV3Client> singleClients = new CopyOnWriteArrayList<>();

  public MqttMessageMetadataRegistrar(MqttConnectOptions options) {
    this.options = options;
    this.setMetadataHandler(this);
    this.setResolver(new AnnotationResolverImpl(MqttMessageListener.class));
  }

  @Override
  public void destroy() {
    singleClients.forEach(c -> CatchUtils.tryThrow(c::disconnect, (Consumer<Throwable>) Throwable::printStackTrace));
    CatchUtils.tryThrow(getClient()::disconnect, (Consumer<Throwable>) Throwable::printStackTrace);
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      MqttMessageListener listener = metadata.getFirstAnnotation(MqttMessageListener.class);
      if (listener.singleClient() || StringUtils.isNotBlank(listener.serverURI())) {
        // 单独的客户端
        String prefix = StringUtils.isNotBlank(listener.clientIdPrefix()) ? listener.clientIdPrefix() : getPrefix();
        String id = IdUtils.nextLowerLetterId(prefix, null, 12);
        MqttConnectOptions opts = BeanHelper.copy(getOptions());
        if (StringUtils.isNotBlank(listener.serverURI())) {
          String serverURI;
          try {
            new URI(listener.serverURI());
            serverURI = listener.serverURI();
          } catch (Exception e) {
            serverURI = SpringCtxHolder.getEnvProperty(listener.serverURI());
            if (StringUtils.isBlank(serverURI)) {
              throw new IllegalArgumentException("指定的MQTT地址错误: " + listener.serverURI());
            }
          }
          opts.setServerURIs(new String[]{serverURI});
        }
        PahoMqttV3Client client = new PahoMqttV3Client(opts, id);
        client.setExecutor(EventLoop.newSingle(false));
        client.getExecutor().execute(() -> {/* ^_^ */});
        // 自动重连
        client.setAutoReconnect(true);
        // 重新连接的间隔
        client.setDelay(5000);
        // 消息分发器，自动重连等
        PahoMqttV3Dispatcher dispatcher = new PahoMqttV3Dispatcher();
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
                           PahoMqttV3Dispatcher dispatcher,
                           MqttMessageListener listener) {
    final SimpleMethodInvoker invoker = new SimpleMethodInvoker(metadata.getBean(), metadata.getMethod());
    MqttMessageSubscriber<MqttMessage> subscriber = (topic, msg) -> {
      Message<?> message = getMessageConverter().toMessage(topic, msg);
      if (listener.async()) {
        EventLoop.asyncIO(() -> {
          try {
            invoker.invoke(topic, msg.getPayload(), message, msg, message.getHeaders());
          } catch (Throwable e) {
            log.error("mqtt error: " + e.getMessage(), e);
          }
        });
      } else {
        try {
          invoker.invoke(topic, msg.getPayload(), message, msg, message.getHeaders());
        } catch (Throwable e) {
          log.error("mqtt error: " + e.getMessage(), e);
        }
      }
    };
    // dispatcher.subscribe(topics, subscriber);
    Stream.of(listener.topics())
        .map(String::trim)
        .filter(StringUtils::isNotBlank)
        .map(topic -> (topic.startsWith("${") || topic.startsWith("#{")) && topic.endsWith("}")
            ? SpringCtxHolder.getEnvProperty(topic)
            : topic)
        .forEach(topic -> dispatcher.subscribe(topic, subscriber));

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

  public PahoMqttV3Dispatcher getDispatcher() {
    return dispatcher;
  }

  public void setDispatcher(PahoMqttV3Dispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

}
