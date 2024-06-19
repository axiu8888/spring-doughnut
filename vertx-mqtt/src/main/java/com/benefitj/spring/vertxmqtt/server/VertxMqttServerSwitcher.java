package com.benefitj.spring.vertxmqtt.server;

import com.benefitj.spring.listener.AppStateListener;
import com.benefitj.vertx.mqtt.server.VertxMqttServer;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;

public class VertxMqttServerSwitcher implements AppStateListener {

  final Logger log = LoggerFactory.getLogger(getClass());

  Vertx vertx;

  VertxMqttServer wsServer;

  VertxMqttServer tcpServer;

  public VertxMqttServerSwitcher(Vertx vertx, VertxMqttServer wsServer, VertxMqttServer tcpServer) {
    this.vertx = vertx;
    this.wsServer = wsServer;
    this.tcpServer = tcpServer;
  }

  @Override
  public void onAppStart(ApplicationReadyEvent evt) throws Exception {
    if (tcpServer != null) {
      try {
        tcpServer.deploy(vertx)
            .onComplete(event -> log.info("Successful start mqtt[tcp] port: {}", tcpServer.getOptions().getPort()))
            .onFailure(event -> log.info("Fail start mqtt[tcp] port: {}, cause: {}", tcpServer.getOptions().getPort(), event.getMessage()));
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
      }
    }
    if (wsServer != null) {
      try {
        wsServer.deploy(vertx)
            .onComplete(event -> log.info("Successful start mqtt[ws] port: {}", wsServer.getOptions().getPort()))
            .onFailure(event -> log.info("Fail start mqtt[ws] port: {}, cause: {}", wsServer.getOptions().getPort(), event.getMessage()));
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
      }
    }
  }

  @Override
  public void onAppStop(ContextClosedEvent evt) throws Exception {
    if (tcpServer != null) {
      tcpServer.stop();
      log.info("stop mqtt[tcp] port: {}", tcpServer.getOptions().getPort());
    }
    if (wsServer != null) {
      wsServer.stop();
      log.info("stop mqtt[ws] port: {}", wsServer.getOptions().getPort());
    }
  }

}
