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

  private final Map<String, IMqttClient> syncClients = new ConcurrentHashMap<>();
  private final Map<String, IMqttAsyncClient> asyncClients = new ConcurrentHashMap<>();

  private MqttPahoClientFactory factory;

  public MqttPahoClientFactoryWrapper(MqttPahoClientFactory factory) {
    this.factory = factory;
  }

  @Override
  public IMqttClient getClientInstance(String url, String clientId) throws MqttPahoClientException {
    if (this.factory instanceof MqttPahoClientFactoryWrapper) {
      try {
        return this.factory.getClientInstance(url, clientId);
      } catch (MqttException e) {
        throw new MqttPahoClientException(e);
      }
    }
    IMqttClient client = syncClients.get(clientId);
    if (client != null && !client.isConnected()) {
      try {
        try {
          client.reconnect();
        } catch (NoSuchMethodError e) {
          client.connect();
        }
      } catch (Exception e) {
        syncClients.remove(clientId);
        client = null;
      }
    }
    if (client == null) {
      client = syncClients.computeIfAbsent(clientId, s -> {
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
  public IMqttAsyncClient getAsyncClientInstance(String url, String clientId) throws MqttPahoClientException {
    if (this.factory instanceof MqttPahoClientFactoryWrapper) {
      try {
        return this.factory.getAsyncClientInstance(url, clientId);
      } catch (MqttException e) {
        throw new MqttPahoClientException(e);
      }
    }
    IMqttAsyncClient client = asyncClients.get(clientId);
    if (client != null && !client.isConnected()) {
      try {
        try {
          client.reconnect();
        } catch (NoSuchMethodError e) {
          client.connect();
        }
      } catch (Exception e) {
        asyncClients.remove(clientId);
        client = null;
      }
    }
    if (client == null) {
      client = asyncClients.computeIfAbsent(clientId, s -> {
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

  public Map<String, IMqttClient> getSyncClients() {
    return syncClients;
  }

  public Map<String, IMqttAsyncClient> getAsyncClients() {
    return asyncClients;
  }
}
