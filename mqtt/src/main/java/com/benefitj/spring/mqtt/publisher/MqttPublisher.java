package com.benefitj.spring.mqtt.publisher;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.ProxyUtils;
import com.benefitj.mqtt.IMqttPublisher;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.mqtt.MqttOptions;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MQTT发布端的代理
 */
public class MqttPublisher implements IMqttPublisher, InitializingBean, DisposableBean {

  private final IMqttClient proxy = ProxyUtils.newListProxy(IMqttClient.class);

  public MqttPublisher(String[] serverURIs, MqttOptions options, String prefix, int count) {
    // 创建多个客户端
    getProxyList().addAll(Stream.of(serverURIs)
        .filter(StringUtils::isNotBlank)
        .map(uris -> {
          MqttOptions copy = BeanHelper.copy(options);
          copy.setServerURIs(uris);
          return new SingleMqttPublisher(options, prefix, count);
        })
        .collect(Collectors.toList()));
  }

  public IMqttClient getProxy() {
    return proxy;
  }

  public List<SingleMqttPublisher> getProxyList() {
    return (List<SingleMqttPublisher>) proxy;
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
  public void publish(Collection<String> topics, MqttMessage msg) {
    getProxyList().forEach(mp -> mp.publish(topics, msg));
  }

  @Override
  public void publishAsync(Collection<String> topics, MqttMessage msg) {
    getProxyList().forEach(mp -> mp.publishAsync(topics, msg));
  }

}
