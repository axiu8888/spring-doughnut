package com.benefitj.spring.vertxmqtt.server;

import com.benefitj.mqtt.vertx.VertxHolder;
import com.benefitj.mqtt.vertx.server.MqttEndpointHandler;
import com.benefitj.mqtt.vertx.server.MqttEndpointHandlerImpl;
import com.benefitj.mqtt.vertx.server.MqttServerProperty;
import com.benefitj.mqtt.vertx.server.VertxMqttServer;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.listener.AppStateListener;
import com.benefitj.spring.listener.AppStateListenerWrapper;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

  static final Logger log = LoggerFactory.getLogger(MqttServerConfiguration.class);

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
  @ConditionalOnMissingBean(name = "mqttServerOptions")
  @Bean("mqttServerOptions")
  public MqttServerOptions mqttServerOptions() {
    return new MqttServerOptions();
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
  @ConditionalOnProperty(prefix = "spring.mqtt.server", value = "wsEnable", matchIfMissing = true)
  @ConditionalOnMissingBean(name = "wsMqttServer")
  @Bean("wsMqttServer")
  public VertxMqttServer wsMqttServer(@Qualifier("mqttServerOptions") MqttServerOptions options,
                                      @Qualifier("mqttEndpointHandler") MqttEndpointHandler mqttEndpointHandler) {
    MqttServerProperty property = BeanHelper.copy(options, new MqttServerProperty());
    property.setPort(options.getWsPort());
    property.setUseWebSocket(true);
    VertxMqttServer server = new VertxMqttServer();
    server.setProperty(property);
    server.setEndpointHandler(mqttEndpointHandler);
    return server;
  }

  /**
   * MQTT服务端
   */
  @ConditionalOnProperty(prefix = "spring.mqtt.server", value = "tcpEnable", matchIfMissing = true)
  @ConditionalOnMissingBean(name = "tcpMqttServer")
  @Bean("tcpMqttServer")
  public VertxMqttServer tcpMqttServer(@Qualifier("mqttServerOptions") MqttServerOptions options,
                                       @Qualifier("mqttEndpointHandler") MqttEndpointHandler mqttEndpointHandler) {
    MqttServerProperty property = BeanHelper.copy(options, new MqttServerProperty());
    property.setPort(options.getTcpPort());
    property.setUseWebSocket(false);
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
                                                  @Autowired(required = false) @Qualifier("wsMqttServer") VertxMqttServer wsServer,
                                                  @Autowired(required = false) @Qualifier("tcpMqttServer") VertxMqttServer tcpServer) {
    return new AppStateListenerWrapper(
        // 部署
        e -> {
          if (tcpServer != null) {
            try {
              tcpServer.deploy(vertx)
                  .onComplete(event -> {
                    log.info("Successful start mqtt[tcp] port: {}", tcpServer.getOptions().getPort());
                  })
                  .onFailure(event -> {
                    log.info("Fail start mqtt[tcp] port: {}, cause: {}", tcpServer.getOptions().getPort(), event.getMessage());
                  });
            } catch (Exception ex) {
              log.error(ex.getMessage(), ex);
            }
          }
          if (wsServer != null) {
            try {
              wsServer.deploy(vertx)
                  .onComplete(event -> {
                    log.info("Successful start mqtt[ws] port: {}", wsServer.getOptions().getPort());
                  })
                  .onFailure(event -> {
                    log.info("Fail start mqtt[ws] port: {}, cause: {}", wsServer.getOptions().getPort(), event.getMessage());
                  });
            } catch (Exception ex) {
              log.error(ex.getMessage(), ex);
            }
          }
        },
        // 停止，
        e -> {
          if (tcpServer != null) {
            tcpServer.stop();
            log.info("stop mqtt[tcp] port: {}", tcpServer.getOptions().getPort());
          }
          if(wsServer != null) {
            wsServer.stop();
            log.info("stop mqtt[ws] port: {}", wsServer.getOptions().getPort());
          }
        }
    );
  }

}
