package com.benefitj.examples;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

public class WebSocketTest {
  public static void main(String[] args) {
    new WebSocketTest().testWebSocketClient();
  }

  public void setup() {
  }

  public void testWebSocketClient() {
    final CountDownLatch latch = new CountDownLatch(1);

//    URI uri = URI.create("http://localhost:8080/api/sockets/spring");
    URI uri = URI.create("http://localhost:8080/api/sockets/javax");
//    URI uri = URI.create("http://localhost:8080/sockets/javax");
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
