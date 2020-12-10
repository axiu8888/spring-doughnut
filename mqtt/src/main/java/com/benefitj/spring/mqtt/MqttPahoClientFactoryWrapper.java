package com.benefitj.spring.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.integration.mqtt.core.ConsumerStopAction;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MqttPahoClientFactoryWrapper implements MqttPahoClientFactory {

  private final Map<String, IMqttClient> mqttClientCache = new ConcurrentHashMap<>();
  private final Map<String, IMqttAsyncClient> mqttAsyncClientCache = new ConcurrentHashMap<>();

  private MqttPahoClientFactory factory;

  public MqttPahoClientFactoryWrapper(MqttPahoClientFactory factory) {
    this.factory = factory;
  }

  @Override
  public IMqttClient getClientInstance(String url, String clientId) throws MqttException {
    if (this.factory instanceof MqttPahoClientFactoryWrapper) {
      return this.factory.getClientInstance(url, clientId);
    }
    IMqttClient client = mqttClientCache.get(clientId);
    if (client != null && !client.isConnected()) {
      try {
        client.reconnect();
      } catch (Exception e) {
        mqttClientCache.remove(clientId);
        client = null;
      }
    }
    if (client == null) {
      client = mqttClientCache.computeIfAbsent(clientId, s -> {
        try {
          return getFactory().getClientInstance(url, clientId);
        } catch (MqttException e) {
          throw new IllegalStateException(e);
        }
      });
    }
    return client;
  }

  @Override
  public IMqttAsyncClient getAsyncClientInstance(String url, String clientId) throws MqttException {
    if (this.factory instanceof MqttPahoClientFactoryWrapper) {
      return this.factory.getAsyncClientInstance(url, clientId);
    }
    IMqttAsyncClient client = mqttAsyncClientCache.get(clientId);
    if (client != null && !client.isConnected()) {
      try {
        client.reconnect();
      } catch (Exception e) {
        mqttAsyncClientCache.remove(clientId);
        client = null;
      }
    }
    if (client == null) {
      client = mqttAsyncClientCache.computeIfAbsent(clientId, s -> {
        try {
          return getFactory().getAsyncClientInstance(url, clientId);
        } catch (MqttException e) {
          throw new IllegalStateException(e);
        }
      });
    }
    return client;
  }

  @Override
  public MqttConnectOptions getConnectionOptions() {
    return getFactory().getConnectionOptions();
  }

  @Override
  public ConsumerStopAction getConsumerStopAction() {
    return null;
  }

  public MqttPahoClientFactory getFactory() {
    return factory;
  }

  public void setFactory(MqttPahoClientFactory factory) {
    this.factory = factory;
  }

  public Map<String, IMqttClient> getMqttClientCache() {
    return mqttClientCache;
  }

  public Map<String, IMqttAsyncClient> getMqttAsyncClientCache() {
    return mqttAsyncClientCache;
  }
}
