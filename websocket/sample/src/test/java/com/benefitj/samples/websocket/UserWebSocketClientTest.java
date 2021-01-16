package com.benefitj.samples.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

/**
 * 测试WebSocket
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserWebSocketClientTest {

  private WebSocketClient client;

  @BeforeAll
  public void before() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);

    URI uri = URI.create("http://127.0.0.1:8080/api/websocket/users");
    client = new WebSocketClient(uri) {
      @Override
      public void onOpen(ServerHandshake handshakedata) {
        latch.countDown();
        System.err.println("onOpen, HttpStatus: " + handshakedata.getHttpStatus() + ", HttpStatusMessage: " + handshakedata.getHttpStatusMessage());
      }

      @Override
      public void onMessage(String message) {
        System.err.println("onMessage, message: " + message);
      }

      @Override
      public void onClose(int code, String reason, boolean remote) {
        System.err.println("onClose, code: " + code + ", message: " + reason + ", remote: " + remote);
        latch.countDown();
      }

      @Override
      public void onError(Exception ex) {
        System.err.println("onError, ex: " + ex.getMessage());
        ex.printStackTrace();
      }
    };

    try {
      client.connect();
      latch.await();
    } catch (InterruptedException e) {}
  }

  @Test
  public void testUserWebSocket() {
    try {
      if (client.isOpen()) {
        for (int i = 0; i < 10; i++) {
          client.send("msg: " + i);
          Thread.sleep(1000);
        }
      }
    } catch (InterruptedException e) {}

  }

  @AfterAll
  public void after() throws Exception {
    client.close();
  }


} 
