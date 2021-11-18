package com.benefitj.spring.vertxmqtt.server;

import com.benefitj.mqtt.VertxHolder;
import com.benefitj.mqtt.server.MqttEndpointHandler;
import com.benefitj.mqtt.server.MqttEndpointHandlerImpl;
import com.benefitj.mqtt.server.MqttServerProperty;
import com.benefitj.mqtt.server.VertxMqttServer;
import com.benefitj.spring.listener.AppStateListener;
import com.benefitj.spring.listener.AppStateListenerWrapper;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQTT 服务端
 */
@EnableConfigurationProperties
@Configuration
public class MqttServerConfiguration {

  /**
   * vertx
   */
  @ConditionalOnMissingBean(name = "vertx")
  @Bean("vertx")
  public Vertx vertx() {
    return VertxHolder.getInstance();
  }

  /**
   * MQTT消息处理
   */
  @ConfigurationProperties(prefix = "spring.mqtt.server")
  @ConditionalOnMissingBean(name = "mqttServerProperty")
  @Bean("mqttServerProperty")
  public MqttServerProperty mqttServerProperty() {
    return new MqttServerProperty();
  }

  /**
   * MQTT客户端状态、消息处理
   */
  @ConditionalOnMissingBean(name = "mqttEndpointHandler")
  @Bean("mqttEndpointHandler")
  public MqttEndpointHandler mqttEndpointHandler() {
    return new MqttEndpointHandlerImpl();
  }

  /**
   * MQTT服务端
   */
  @ConditionalOnMissingBean(name = "mqttServer")
  @Bean("mqttServer")
  public VertxMqttServer mqttServer(@Qualifier("mqttServerProperty") MqttServerProperty property,
                                    @Qualifier("mqttEndpointHandler") MqttEndpointHandler mqttEndpointHandler) {
    VertxMqttServer server = new VertxMqttServer();
    server.setProperty(property);
    server.setEndpointHandler(mqttEndpointHandler);
    return server;
  }

  /**
   * MQTT启动器
   */
  @ConditionalOnMissingBean(name = "vertxMqttServerSwitcher")
  @Bean("vertxMqttServerSwitcher")
  public AppStateListener vertxMqttServerSwitcher(@Qualifier("vertx") Vertx vertx,
                                                  @Qualifier("mqttServer") VertxMqttServer server) {
    return new AppStateListenerWrapper(
        // 部署
        e -> server.deploy(vertx),
        // 停止，
        e -> server.stop()
    );
  }

}
