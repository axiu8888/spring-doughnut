package com.benefitj.spring.vertxmqtt.subscriber;

import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.AnnotationResolverImpl;
import com.benefitj.spring.annotation.MetadataHandler;
import com.benefitj.vertx.mqtt.client.VertxMqttMessageDispatcher;

import java.util.List;

/**
 * 注册器
 */
public class MqttSubscriberRegistrar extends AnnotationBeanProcessor implements MetadataHandler {

  private VertxMqttMessageDispatcher dispatcher;

  public MqttSubscriberRegistrar() {
    this.setMetadataHandler(this);
    this.setResolver(new AnnotationResolverImpl(MqttSubscriber.class));
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      MqttSubscriber subscriber = metadata.getFirstAnnotation(MqttSubscriber.class);
      SimpleMethodInvoker invoker = new SimpleMethodInvoker(metadata.getBean(), metadata.getMethod());
      getDispatcher().subscribe(subscriber.value(), invoker::invoke);
    }
  }

  public VertxMqttMessageDispatcher getDispatcher() {
    return dispatcher;
  }

  public void setDispatcher(VertxMqttMessageDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }

}
