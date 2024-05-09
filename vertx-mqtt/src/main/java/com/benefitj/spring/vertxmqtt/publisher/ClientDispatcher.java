package com.benefitj.spring.vertxmqtt.publisher;

import com.benefit.vertx.mqtt.client.VertxMqttClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface ClientDispatcher {

  /**
   * 分派客户端
   *
   * @param clients 客户端
   * @return 分派的客户端
   */
  VertxMqttClient dispatch(List<VertxMqttClient> clients);


  static ClientDispatcher create() {
    return new Impl();
  }

  class Impl implements ClientDispatcher {

    final AtomicInteger index = new AtomicInteger(0);

    @Override
    public VertxMqttClient dispatch(List<VertxMqttClient> clients) {
      if (clients.isEmpty()) {
        throw new IllegalStateException("clients is empty!");
      }
      int size = clients.size();
      VertxMqttClient vmc = null;
      for (int i = 0; i < size; i++) {
        int pos = index.getAndIncrement();
        if (pos > 1000_000) { index.set(0); }
        vmc = clients.get(pos % size);
        if (vmc.isConnected()) {
          return vmc;
        }
      }
      return vmc;
    }

  }
}