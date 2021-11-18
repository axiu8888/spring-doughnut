package com.benefitj.spring.vertxmqtt.subscriber;

import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.mqtt.client.VertxMqttMessageDispatcher;
import com.benefitj.spring.annotationprcoessor.AnnotationBeanProcessor;
import com.benefitj.spring.annotationprcoessor.AnnotationMetadata;
import com.benefitj.spring.annotationprcoessor.MetadataHandler;

import java.util.List;

/**
 * 注册器
 */
public class MqttSubscriberRegistrar extends AnnotationBeanProcessor implements MetadataHandler {

  private VertxMqttMessageDispatcher dispatcher;

  public MqttSubscriberRegistrar() {
    this.setAnnotationType(MqttSubscriber.class);
    this.setMetadataHandler(this);
  }

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      MqttSubscriber subscriber = (MqttSubscriber) metadata.getAnnotation();
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
