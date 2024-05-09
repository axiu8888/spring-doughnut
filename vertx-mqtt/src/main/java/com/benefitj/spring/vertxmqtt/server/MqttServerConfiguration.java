package com.benefitj.spring.vertxmqtt.server;

import com.benefit.vertx.VertxHolder;
import com.benefit.vertx.mqtt.server.*;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.listener.AppStateListener;
import com.benefitj.spring.listener.EnableAppStateListener;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * MQTT 服务端
 */
@EnableConfigurationProperties
@EnableAppStateListener
@Configuration
public class MqttServerConfiguration {

  /**
   * vertx
   */
  @ConditionalOnMissingBean(name = "vertx")
  @Bean("vertx")
  public Vertx vertx() {
    return VertxHolder.getVertx();
  }

  /**
   * MQTT消息处理
   */
  @ConfigurationProperties(prefix = "spring.mqtt.server")
  @ConditionalOnMissingBean
  @Bean
  public MqttServerOptions mqttServerOptions() {
    return new MqttServerOptions();
  }

  /**
   * MQTT客户端状态
   */
  @ConditionalOnMissingBean
  @Bean
  public VertxMqttEndpointManager mqttEndpointManager() {
    return VertxMqttEndpointManager.get();
  }

  /**
   * MQTT客户端状态、消息处理
   */
  @ConditionalOnMissingBean
  @Bean
  public MqttEndpointHandler mqttEndpointHandler() {
    return new MqttEndpointHandlerImpl();
  }

  /**
   * MQTT服务端
   */
  @ConditionalOnProperty(prefix = "spring.mqtt.server", name = "ws-enable")
  @ConditionalOnMissingBean(name = "wsMqttServer")
  @Bean("wsMqttServer")
  public VertxMqttServer wsMqttServer(VertxMqttEndpointManager mqttEndpointManager,
                                      @Qualifier("mqttServerOptions") MqttServerOptions options,
                                      @Qualifier("mqttEndpointHandler") MqttEndpointHandler mqttEndpointHandler) {
    MqttServerProperty property = BeanHelper.copy(options, new MqttServerProperty());
    property.setPort(options.getWsPort());
    property.setUseWebSocket(true);
    VertxMqttServer server = new VertxMqttServer();
    server.setAuthenticator(endpoint -> true);  // 认证
    server.setProperty(property);
    server.setEndpointManager(mqttEndpointManager);
    server.setEndpointHandler(mqttEndpointHandler);
    return server;
  }

  /**
   * MQTT服务端
   */
  @ConditionalOnProperty(prefix = "spring.mqtt.server", name = "tcp-enable")
  @ConditionalOnMissingBean(name = "tcpMqttServer")
  @Bean("tcpMqttServer")
  public VertxMqttServer tcpMqttServer(VertxMqttEndpointManager mqttEndpointManager,
                                       @Qualifier("mqttServerOptions") MqttServerOptions options,
                                       @Qualifier("mqttEndpointHandler") MqttEndpointHandler mqttEndpointHandler) {
    MqttServerProperty property = BeanHelper.copy(options, new MqttServerProperty());
    property.setPort(options.getTcpPort());
    property.setUseWebSocket(false);
    VertxMqttServer server = new VertxMqttServer();
    server.setAuthenticator(endpoint -> true);  // 认证
    server.setProperty(property);
    server.setEndpointManager(mqttEndpointManager);
    server.setEndpointHandler(mqttEndpointHandler);
    return server;
  }

  /**
   * MQTT启动器
   */
  @Lazy(value = false)
  @ConditionalOnMissingBean
  @Bean
  public AppStateListener vertxMqttServerSwitcher(@Qualifier("vertx") Vertx vertx,
                                                  @Qualifier("wsMqttServer") @Autowired(required = false) VertxMqttServer wsServer,
                                                  @Qualifier("tcpMqttServer") @Autowired(required = false) VertxMqttServer tcpServer) {
    return new VertxMqttServerSwitcher(vertx, wsServer, tcpServer);
  }

}
