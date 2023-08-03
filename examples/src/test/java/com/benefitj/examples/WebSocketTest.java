package com.benefitj.examples;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class WebSocketTest {

  public void setup() {
  }

  @Test
  public void testWebSocketClient() {
    final CountDownLatch latch = new CountDownLatch(1);

    //URI uri = URI.create("ws://127.0.0.1:80/api/sockets/simple");
    URI uri = URI.create("/devtools/browser/87af382c-857c-40f5-a313-0f6e44144e8b");
    WebSocketClient client = new WebSocketClient(uri) {
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

    client.connect();

    try {
      latch.await();

      if (client.isOpen()) {
        for (int i = 0; i < 10; i++) {
          client.send("hehe: " + i);
          Thread.sleep(1000);
        }
      }
    } catch (InterruptedException e) {}

    client.close();

  }

  public void tearDown() {
  }


}
