package com.benefitj.spring.mqtt.config;

import com.benefitj.core.IdUtils;
import com.benefitj.spring.mqtt.MqttMessageListenerEndpointRegistry;
import com.benefitj.spring.mqtt.MqttOptionsProperty;
import com.benefitj.spring.mqtt.MqttPahoClientFactoryWrapper;
import com.benefitj.spring.mqtt.annotaion.MqttMessageListenerEndpoint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.*;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.scheduling.TaskScheduler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultMqttMessageListenerEndpointRegistry implements MqttMessageListenerEndpointRegistry,
    BeanFactoryAware, ApplicationEventPublisherAware, ApplicationContextAware, SmartLifecycle, DisposableBean {


  private ConfigurableListableBeanFactory beanFactory;

  private TaskScheduler taskScheduler;

  private ApplicationEventPublisher applicationEventPublisher;

  private ApplicationContext applicationContext;
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
  private MqttPahoClientFactoryWrapper clientFactoryDelegate;
  /**
   * adapter
   */
  private final List<MqttPahoMessageDrivenChannelAdapter> channelAdapters = Collections.synchronizedList(new ArrayList<>());
  /**
   * 执行状态
   */
  private volatile boolean running = false;

  public DefaultMqttMessageListenerEndpointRegistry() {
  }

  public DefaultMqttMessageListenerEndpointRegistry(MqttOptionsProperty property,
                                                    MqttPahoClientFactory clientFactory) {
    this.property = property;
    this.clientFactory = clientFactory;
  }

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    if (beanFactory instanceof ConfigurableListableBeanFactory) {
      this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
      this.taskScheduler = beanFactory.getBean(TaskScheduler.class);
    }
  }

  @Override
  public void start() {
    // 启动客户端
    for (MqttPahoMessageDrivenChannelAdapter adapter : channelAdapters) {
      try {
        adapter.start();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    this.running = true;
  }

  @Override
  public void stop() {
    for (MqttPahoMessageDrivenChannelAdapter adapter : channelAdapters) {
      try {
        if (adapter.isRunning()) {
          adapter.stop();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    this.running = false;
  }

  @Override
  public boolean isRunning() {
    return this.running;
  }

  @Override
  public void destroy() throws Exception {
    stop();
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void registerEndpoint(Object bean, Method method, MqttMessageListenerEndpoint endpoint, MessageHandlerMethodFactory handlerMethodFactory) {
    String beanName = method.getDeclaringClass() + "_" + method.getName();
    String clientId = generateId(property.getClientIdPrefix());
    MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, getClientFactoryDelegate());
    adapter.setCompletionTimeout(property.getCompletionTimeout());
    adapter.setRecoveryInterval(property.getRecoveryInterval());
    DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
    converter.setBeanFactory(beanFactory);
    converter.setPayloadAsBytes(true);
    adapter.setConverter(converter);
    adapter.setApplicationEventPublisher(applicationEventPublisher);
    adapter.setApplicationContext(applicationContext);
    adapter.setBeanName(beanName);

    // 服务质量
    adapter.setQos(property.getQos());
    // 设置订阅通道
    DirectChannel mqttOutputChannel = new DirectChannel();
    mqttOutputChannel.setBeanName(beanName + "MqttOutputChannel");
    InvocableHandlerMethod handlerMethod = handlerMethodFactory.createInvocableHandlerMethod(bean, method);
    mqttOutputChannel.subscribe(message -> {
      try {
        handlerMethod.invoke(message, endpoint, adapter);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    });
    adapter.setOutputChannel(mqttOutputChannel);
    adapter.setErrorChannelName(beanName + "ErrorChannelName");
    adapter.setErrorChannel((message, timeout) -> {
      // ignore
      return true;
    });

    adapter.setTaskScheduler(taskScheduler);

    for (String topic : endpoint.topics()) {
      adapter.addTopic(topic, endpoint.qos());
    }

    channelAdapters.add(adapter);
    // 注册
    beanFactory.registerSingleton(adapter.getBeanName(), adapter);
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

  public MqttPahoClientFactoryWrapper getClientFactoryDelegate() {
    MqttPahoClientFactoryWrapper delegate = this.clientFactoryDelegate;
    if (delegate == null) {
      synchronized (this) {
        if ((delegate = this.clientFactoryDelegate) == null) {
          delegate = (this.clientFactoryDelegate = new MqttPahoClientFactoryWrapper(clientFactory));
        }
      }
    }
    return delegate;
  }

  public MqttPahoClientFactory getClientFactory() {
    return clientFactory;
  }

  public void setClientFactory(MqttPahoClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }

}
