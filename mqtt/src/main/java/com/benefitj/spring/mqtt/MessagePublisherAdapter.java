package com.benefitj.spring.mqtt;

import com.benefitj.event.BaseEventAdapter;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 消息发送
 */
public class MessagePublisherAdapter extends BaseEventAdapter<MqttPublishMessage> {

  private MqttPublisher mqttPublisher;

  public MessagePublisherAdapter() {
  }

  public MessagePublisherAdapter(MqttPublisher mqttPublisher) {
    this.setMqttPublisher(mqttPublisher);
  }

  @Override
  public void process(MqttPublishMessage msg) {
    MqttMessage mm = new MqttMessage(msg.getPayload());
    mm.setId(msg.getMessageId());
    mm.setQos(msg.getQos());
    mm.setRetained(msg.isRetained());
    mqttPublisher.send(msg.getTopics(), mm);
  }

  public MqttPublisher getMqttPublisher() {
    return mqttPublisher;
  }

  public void setMqttPublisher(MqttPublisher mqttPublisher) {
    this.mqttPublisher = mqttPublisher;
  }
}
