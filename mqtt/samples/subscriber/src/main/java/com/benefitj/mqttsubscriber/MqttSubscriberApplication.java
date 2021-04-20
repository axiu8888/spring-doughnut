package com.benefitj.mqttsubscriber;

import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.spring.mqtt.IMqttCallback;
import com.benefitj.spring.mqtt.MqttHeaders;
import com.benefitj.spring.mqtt.MqttMessageListener;
import com.benefitj.spring.mqtt.SingleMqttClient;
import com.benefitj.spring.mqtt.EnableMqttSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * MQTT消息订阅
 */
@EnableMqttSubscriber
@SpringBootApplication
public class MqttSubscriberApplication {
  public static void main(String[] args) {
    SpringApplication.run(MqttSubscriberApplication.class, args);
  }

  @Slf4j
  @Component
  public static class DefaultMqttMessageSubscriber {

    @MqttMessageListener(topics = "/device/+", clientIdPrefix = "mqtt-subscriber-")
    public void handleMessage(Message<?> message) throws MessagingException {
      log.info("{}, payload: {}"
          , MqttHeaders.of(message.getHeaders())
          , new String((byte[]) message.getPayload())
      );
    }
  }

//  @Slf4j
//  @Component
//  public static class DefaultMqttMessageSubscriber2 {
//
//    @MqttMessageListener(topics = "/collector/device/010003b8", clientIdPrefix = "handleMessage-")
//    public void handleMessage(Message<?> message) throws MessagingException {
//      log.info("{}, payload: {}"
//          , MqttHeaders.of(message.getHeaders())
//          , new String((byte[]) message.getPayload())
//      );
//    }
//  }

  @Slf4j
  @Component
  public static class OnStarter implements InitializingBean, IMqttCallback {

    @Autowired
    private MqttPahoClientFactory clientFactory;

    private SingleMqttClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
      MqttPahoClientFactory factory = this.clientFactory;
      String clientId = IdUtils.nextLowerLetterId("mqtt-sender-", null, 6);
      this.client = new SingleMqttClient(factory, clientId);
      client.setCallback(this);
      client.subscribe("/collector/device/010003b8", 0);
      // 调度
      client.setExecutor(EventLoop.newSingle(true));
    }

    @Override
    public void connectionLost(Throwable cause) {
      log.error("connectionLost: " + cause.getMessage(), cause);
      log.info("client status: {}, cause: {}", client.isConnected(), cause.getClass());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
      // 缓存数据
      log.info("topic: {}, msg: {}", topic, new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
      // ignore
      log.info("deliveryComplete: {}", token.getMessageId());
    }

    @Override
    public void onSubscribeAgain(SingleMqttClient client) {
      log.info("重新订阅: {}", client.getClientId());
      // 订阅
      client.subscribe("/collector/device/010003b8", 0);
    }
  }
}
