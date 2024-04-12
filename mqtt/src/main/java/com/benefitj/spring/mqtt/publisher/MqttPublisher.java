package com.benefitj.spring.mqtt.publisher;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.ProxyUtils;
import com.benefitj.spring.BeanHelper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MQTT发布端的代理
 */
public class MqttPublisher implements IMqttPublisher, InitializingBean, DisposableBean {

  private final IMqttClient proxy = ProxyUtils.newListProxy(IMqttClient.class);

  private String[] serverURIs;

  public MqttPublisher(String[] serverURIs, MqttConnectOptions options, String prefix, int count) {
    this.serverURIs = serverURIs;
    // 创建多个客户端
    getProxyList().addAll(Stream.of(serverURIs)
        .filter(StringUtils::isNotBlank)
        .map(uris -> {
          MqttConnectOptions copy = BeanHelper.copy(options);
          copy.setServerURIs(new String[]{uris});
          return new MqttPublisherImpl(copy, prefix, count);
        })
        .collect(Collectors.toList()));
  }

  public IMqttClient getProxy() {
    return proxy;
  }

  public List<MqttPublisherImpl> getProxyList() {
    return (List<MqttPublisherImpl>) proxy;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    getProxyList().forEach(mp -> CatchUtils.tryThrow(mp::afterPropertiesSet, Throwable::printStackTrace));
  }

  @Override
  public void destroy() throws Exception {
    getProxyList().forEach(mp -> CatchUtils.tryThrow(mp::destroy, Throwable::printStackTrace));
  }

  @Override
  public void publish(String topic, MqttMessage msg) throws MqttPublishException {
    getProxyList().forEach(mp -> CatchUtils.tryThrow(() -> mp.publish(topic, msg), Throwable::printStackTrace));
  }

  @Override
  public void publishAsync(String topic, MqttMessage msg) {
    getProxyList().forEach(mp -> CatchUtils.tryThrow(() -> mp.publishAsync(topic, msg), Throwable::printStackTrace));
  }

}
