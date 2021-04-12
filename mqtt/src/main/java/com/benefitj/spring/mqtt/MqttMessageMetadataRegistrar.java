package com.benefitj.spring.mqtt;

import com.benefitj.core.IdUtils;
import com.benefitj.spring.registrar.AnnotationMetadataRegistrar;
import com.benefitj.spring.registrar.AnnotationMetadata;
import com.benefitj.spring.registrar.MethodElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.scheduling.TaskScheduler;

import java.nio.charset.StandardCharsets;

/**
 * MQTT注册器
 */
public class MqttMessageMetadataRegistrar implements
    AnnotationMetadataRegistrar, ApplicationEventPublisherAware, ApplicationContextAware {

  private ApplicationContext applicationContext;
  private ApplicationEventPublisher applicationEventPublisher;
  private TaskScheduler taskScheduler;

  /**
   * 属性配置
   */
  private MqttOptionsProperty property;
  /**
   * 客户端工厂
   */
  private MqttPahoClientFactory clientFactory;
  /**
   * 代理
   */
  private MqttPahoClientFactoryWrapper clientFactoryWrapper;
  /**
   * 方法的消息转换和调用
   */
  private MessageHandlerMethodFactory handlerMethodFactory;

  public MqttMessageMetadataRegistrar(MqttOptionsProperty property,
                                      MqttPahoClientFactory clientFactory) {
    this.property = property;
    this.clientFactory = clientFactory;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    this.taskScheduler = (TaskScheduler) applicationContext.getBean("taskScheduler");
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void register(AnnotationMetadata metadata, ConfigurableListableBeanFactory beanFactory) {
    MessageHandlerMethodFactory methodFactory = getHandlerMethodFactory();
    if (methodFactory == null) {
      setHandlerMethodFactory(methodFactory = createMessageHandlerMethodFactory(beanFactory));
    }
    for (MethodElement element : metadata.getMethodElements()) {
      registerSingleton(metadata, beanFactory, methodFactory, element, (MqttMessageListener) element.getAnnotations()[0]);
    }
  }

  private void registerSingleton(AnnotationMetadata metadata,
                                 ConfigurableListableBeanFactory beanFactory,
                                 MessageHandlerMethodFactory handlerMethodFactory,
                                 MethodElement element,
                                 MqttMessageListener listener) {
    String beanName = metadata.getBeanName();
    String methodName = element.getMethod().getName();
    String clientId = generateId(listener.clientIdPrefix());
    MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, getClientFactoryWrapper());
    adapter.setBeanFactory(beanFactory);
    adapter.setCompletionTimeout(property.getCompletionTimeout());
    adapter.setRecoveryInterval(property.getRecoveryInterval());
    MqttMessageConverter converter;
    try {
      converter = beanFactory.getBean(MqttMessageConverter.class);
    } catch (BeansException e) {
      DefaultPahoMessageConverter pmc = new DefaultPahoMessageConverter();
      pmc.setBeanFactory(beanFactory);
      pmc.setPayloadAsBytes(true);
      converter = pmc;
    }
    adapter.setConverter(converter);
    adapter.setApplicationEventPublisher(applicationEventPublisher);
    adapter.setApplicationContext(applicationContext);
    adapter.setBeanName(beanName + "ChannelAdapter_" + methodName);

    // 服务质量
    adapter.setQos(property.getQos());
    // 设置订阅通道
    DirectChannel mqttOutputChannel = new DirectChannel();
    mqttOutputChannel.setBeanName(beanName + "MqttOutputChannel_" + methodName);
    InvocableHandlerMethod handlerMethod
        = handlerMethodFactory.createInvocableHandlerMethod(metadata.getBean(), element.getMethod());
    mqttOutputChannel.subscribe(message -> {
      try {
        handlerMethod.invoke(message, listener, adapter);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    });
    adapter.setOutputChannel(mqttOutputChannel);
    //adapter.setErrorChannelName(beanName + "ErrorChannelName_" + methodName);
    //adapter.setErrorChannel((message, timeout) -> {
    //  // ignore
    //  return true;
    //});

    adapter.setTaskScheduler(taskScheduler);

    for (String topic : listener.topics()) {
      adapter.addTopic(topic, listener.qos());
    }
    // 注册
    beanFactory.registerSingleton(adapter.getBeanName(), adapter);

    /*logger.info("订阅MQTT消息, clientId: {}, topics: {}, qos: {}, remote: {}"
        , clientId
        , listener.topics()
        , listener.qos()
        , property.getServerURIs()
    );*/
  }

  protected MessageHandlerMethodFactory createMessageHandlerMethodFactory(BeanFactory beanFactory) {
    DefaultMessageHandlerMethodFactory defaultFactory = new DefaultMessageHandlerMethodFactory();
    defaultFactory.setBeanFactory(beanFactory);
    DefaultConversionService conversionService = new DefaultConversionService();
    conversionService.addConverter(new BytesToStringConverter());
    defaultFactory.setConversionService(conversionService);
    defaultFactory.afterPropertiesSet();
    return defaultFactory;
  }

  protected String generateId(String clientIdPrefix) {
    if (StringUtils.isNotBlank(clientIdPrefix)) {
      return clientIdPrefix + IdUtils.nextLowerLetterId(8);
    }
    return IdUtils.nextLowerLetterId(16);
  }

  public MqttOptionsProperty getProperty() {
    return property;
  }

  public void setProperty(MqttOptionsProperty property) {
    this.property = property;
  }

  public MqttPahoClientFactory getClientFactory() {
    return clientFactory;
  }

  public void setClientFactory(MqttPahoClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

  public MessageHandlerMethodFactory getHandlerMethodFactory() {
    return this.handlerMethodFactory;
  }

  public void setHandlerMethodFactory(MessageHandlerMethodFactory handlerMethodFactory) {
    this.handlerMethodFactory = handlerMethodFactory;
  }

  public MqttPahoClientFactoryWrapper getClientFactoryWrapper() {
    MqttPahoClientFactoryWrapper wrapper = this.clientFactoryWrapper;
    if (wrapper == null) {
      synchronized (this) {
        if ((wrapper = this.clientFactoryWrapper) == null) {
          this.clientFactoryWrapper = (wrapper = new MqttPahoClientFactoryWrapper(clientFactory));
        }
      }
    }
    return wrapper;
  }
  public static class BytesToStringConverter implements Converter<byte[], String> {
    @Override
    public String convert(byte[] source) {
      return new String(source, StandardCharsets.UTF_8);
    }
  }

}
