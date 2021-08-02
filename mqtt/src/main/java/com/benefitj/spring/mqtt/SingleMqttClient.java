package com.benefitj.spring.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 简单的MQTT客户端
 */
public class SingleMqttClient implements IMqttClient {

  /**
   * 创建MQTT客户端
   *
   * @param factory  客户端工厂
   * @param clientId 客户端ID
   * @return 返回MQTT客户端对象
   */
  public static IMqttClient provide(MqttPahoClientFactory factory, String clientId) {
    try {
      MqttConnectOptions options = factory.getConnectionOptions();
      clientId = clientId != null ? clientId : UUID.randomUUID().toString().replace("-", "");
      return factory.getClientInstance(options.getServerURIs()[0], clientId);
    } catch (MqttException e) {
      throw new IllegalStateException(e);
    }
  }

  private final MqttCallbackListener listener = new MqttCallbackListener();
  /**
   * 客户端
   */
  private volatile IMqttClient source;
  /**
   * 是否已经断开
   */
  private boolean disconnected = false;
  /**
   * 自动连接的间隔，默认1000毫秒
   */
  private long delay = 1000;
  /**
   * 最新一次尝试连接的时间
   */
  private long lastReconnectTime = 0;
  /**
   * 重新订阅的任务
   */
  private final AtomicReference<ScheduledFuture<?>> subscribeTask = new AtomicReference<>();
  /**
   * 回调
   */
  private MqttCallback callback;
  /**
   * 重新订阅的监听
   */
  private MqttTopicSubscribeAgainListener subscribeAgainListener;
  /**
   * 调度器
   */
  private ScheduledExecutorService executor;

  public SingleMqttClient(MqttPahoClientFactory factory) {
    this(factory, null);
  }

  public SingleMqttClient(MqttPahoClientFactory factory, String clientId) {
    this(provide(factory, clientId), factory.getConnectionOptions());
  }

  public SingleMqttClient(IMqttClient source) {
    this(source, null);
  }

  public SingleMqttClient(IMqttClient source, MqttConnectOptions options) {
    this.source = source;
    tryConnect(source, options);
    source.setCallback(listener);
  }

  /**
   * 尝试连接
   *
   * @param raw     客户都安
   * @param options 连接参数
   */
  protected void tryConnect(IMqttClient raw, MqttConnectOptions options) {
    try {
      if (!raw.isConnected()) {
        if (options != null) {
          raw.connect(options);
        } else {
          raw.connect();
        }
      }
    } catch (MqttException ignore) { /* ~ */ }
  }

  public boolean isDisconnected() {
    return disconnected;
  }

  public void setDisconnected(boolean disconnected) {
    this.disconnected = disconnected;
  }

  protected IMqttClient getSource() {
    IMqttClient c = this.source;
    if (!(isDisconnected() && c.isConnected())) {
      long now = System.currentTimeMillis();
      long delay = this.delay;
      if (now - getLastReconnectTime() <= delay) {
        return c;
      }
      synchronized (this) {
        if (now - getLastReconnectTime() > delay) {
          try {
            if (!c.isConnected()) {
              try {
                c.reconnect();
              } catch (NoSuchMethodError nse) {
                c.connect();
              }
            }
          } catch (MqttException ignore) {
            /* ~ */
          } finally {
            this.setLastReconnectTime(now);
          }
        }
      }
    }
    return c;
  }

  public void setSource(IMqttClient source) {
    this.source = source;
    source.setCallback(listener);
  }

  public long getDelay() {
    return delay;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }

  public long getLastReconnectTime() {
    return lastReconnectTime;
  }

  public void setLastReconnectTime(long lastReconnectTime) {
    this.lastReconnectTime = lastReconnectTime;
  }

  public ScheduledExecutorService getExecutor() {
    return executor;
  }

  public MqttTopicSubscribeAgainListener getSubscribeAgainListener() {
    return subscribeAgainListener;
  }

  public void setSubscribeAgainListener(MqttTopicSubscribeAgainListener subscribeAgainListener) {
    this.subscribeAgainListener = subscribeAgainListener;
  }

  public void setExecutor(ScheduledExecutorService executor) {
    this.executor = executor;
  }

  public <T> T tryCatch(MqttFunction<IMqttClient, T> func) {
    try {
      return func.apply(getSource());
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public void tryCatch(MqttConsumer<IMqttClient> c) {
    try {
      c.accept(getSource());
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void connect() {
    tryCatch((MqttConsumer<IMqttClient>) IMqttClient::connect);
  }

  @Override
  public void connect(MqttConnectOptions options) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.connect(options));
  }

  @Override
  public IMqttToken connectWithResult(MqttConnectOptions options) {
    return tryCatch((MqttFunction<IMqttClient, IMqttToken>) c -> c.connectWithResult(options));
  }

  @Override
  public void disconnect() {
    this.setDisconnected(true);
    tryCatch((MqttConsumer<IMqttClient>) IMqttClient::disconnect);
  }

  @Override
  public void disconnect(long quiesceTimeout) {
    this.setDisconnected(true);
    tryCatch((MqttConsumer<IMqttClient>) c -> c.disconnect(quiesceTimeout));
  }

  @Override
  public void disconnectForcibly() {
    this.setDisconnected(true);
    tryCatch((MqttConsumer<IMqttClient>) IMqttClient::disconnectForcibly);
  }

  @Override
  public void disconnectForcibly(long disconnectTimeout) {
    this.setDisconnected(true);
    tryCatch((MqttConsumer<IMqttClient>) c -> c.disconnectForcibly(disconnectTimeout));
  }

  @Override
  public void disconnectForcibly(long quiesceTimeout, long disconnectTimeout) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.disconnectForcibly(quiesceTimeout, disconnectTimeout));
  }

  @Override
  public void subscribe(String topicFilter) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.subscribe(topicFilter));
  }

  @Override
  public void subscribe(String[] topicFilters) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.subscribe(topicFilters));
  }

  @Override
  public void subscribe(String topicFilter, int qos) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.subscribe(topicFilter, qos));
  }

  @Override
  public void subscribe(String[] topicFilters, int[] qos) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.subscribe(topicFilters, qos));
  }

  @Override
  public void subscribe(String topicFilter, IMqttMessageListener messageListener) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.subscribe(topicFilter, messageListener));
  }

  @Override
  public void subscribe(String[] topicFilters, IMqttMessageListener[] messageListeners) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.subscribe(topicFilters, messageListeners));
  }

  @Override
  public void subscribe(String topicFilter, int qos, IMqttMessageListener messageListener) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.subscribe(topicFilter, qos, messageListener));
  }

  @Override
  public void subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.subscribe(topicFilters, qos, messageListeners));
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter) {
    return tryCatch((MqttFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilter));
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter, IMqttMessageListener messageListener) {
    return tryCatch((MqttFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilter, messageListener));
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter, int qos) {
    return tryCatch((MqttFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilter, qos));
  }

  @Override
  public IMqttToken subscribeWithResponse(String topicFilter, int qos, IMqttMessageListener messageListener) {
    return tryCatch((MqttFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilter, qos, messageListener));
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters) {
    return tryCatch((MqttFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilters));
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters, IMqttMessageListener[] messageListeners) {
    return tryCatch((MqttFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilters, messageListeners));
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos) {
    return tryCatch((MqttFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilters, qos));
  }

  @Override
  public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) {
    return tryCatch((MqttFunction<IMqttClient, IMqttToken>) c -> c.subscribeWithResponse(topicFilters, qos, messageListeners));
  }

  @Override
  public void unsubscribe(String topicFilter) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.unsubscribe(topicFilter));
  }

  @Override
  public void unsubscribe(String[] topicFilters) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.unsubscribe(topicFilters));
  }

  @Override
  public void publish(String topic, byte[] payload, int qos, boolean retained) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.publish(topic, payload, qos, retained));
  }

  @Override
  public void publish(String topic, MqttMessage message) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.publish(topic, message));
  }

  @Override
  public void setCallback(MqttCallback callback) {
    this.callback = callback;
    if (this.subscribeAgainListener == null
        && callback instanceof MqttTopicSubscribeAgainListener) {
      this.subscribeAgainListener = (MqttTopicSubscribeAgainListener) callback;
    }
  }

  @Override
  public MqttTopic getTopic(String topic) {
    return getSource().getTopic(topic);
  }

  @Override
  public boolean isConnected() {
    return getSource().isConnected();
  }

  @Override
  public String getClientId() {
    return getSource().getClientId();
  }

  @Override
  public String getServerURI() {
    return getSource().getServerURI();
  }

  @Override
  public IMqttDeliveryToken[] getPendingDeliveryTokens() {
    return getSource().getPendingDeliveryTokens();
  }

  @Override
  public void setManualAcks(boolean manualAcks) {
    getSource().setManualAcks(manualAcks);
  }

  @Override
  public void reconnect() {
    this.setDisconnected(false);
    tryCatch(IMqttClient::reconnect);
  }

  @Override
  public void messageArrivedComplete(int messageId, int qos) {
    tryCatch((MqttConsumer<IMqttClient>) c -> c.messageArrivedComplete(messageId, qos));
  }

  @Override
  public void close() {
    tryCatch(IMqttClient::close);
  }


  public class MqttCallbackListener implements MqttCallback, Runnable {

    @Override
    public void connectionLost(Throwable cause) {
      try {
        MqttTopicSubscribeAgainListener subscribeAgainListener = getSubscribeAgainListener();
        if (subscribeAgainListener != null) {
          ScheduledFuture<?> oldTask = subscribeTask.get();
          if (oldTask != null) {
            oldTask.cancel(true);
          }
          long delay = Math.max(Math.min(300_000, getDelay()), 500);
          ScheduledFuture<?> newTask = getExecutor().scheduleAtFixedRate(
              this, Math.max(getDelay(), 1), delay, TimeUnit.MILLISECONDS);
          subscribeTask.set(newTask);
        }
      } finally {
        MqttCallback cb = SingleMqttClient.this.callback;
        if (cb != null) {
          cb.connectionLost(cause);
        }
      }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
      MqttCallback cb = SingleMqttClient.this.callback;
      if (cb != null) {
        cb.messageArrived(topic, message);
      }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
      MqttCallback cb = SingleMqttClient.this.callback;
      if (cb != null) {
        cb.deliveryComplete(token);
      }
    }

    @Override
    public void run() {
      ScheduledFuture<?> oldTask = subscribeTask.get();
      if (oldTask != null && isConnected()) {
        if (subscribeTask.compareAndSet(oldTask, null)) {
          try {
            getSubscribeAgainListener().onSubscribeAgain(SingleMqttClient.this);
          } finally {
            oldTask.cancel(true);
          }
        }
      }
    }
  }


  public interface MqttFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws Exception;
  }

  public interface MqttConsumer<T> {
    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws Exception;
  }

}
