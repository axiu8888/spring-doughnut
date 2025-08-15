package com.benefitj.natproxy.tcp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.*;

class TcpProxyTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }


  @Test
  void test() throws Exception {
//    testConnection("172.101.0.239", 80);

    printRoutingTable();
  }

  // 基础连通性测试（无需Netty）
  public static void testConnection(String host, int port) {
    try (Socket s = new Socket()) {
      s.connect(new InetSocketAddress(host, port), 3000);
      System.out.println("✅ TCP连接成功");

      // 尝试发送HTTP请求
      OutputStream out = s.getOutputStream();
      out.write(("GET / HTTP/1.1\r\nHost: " + host + "\r\n\r\n").getBytes());
      // 读取响应
      BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
      System.out.println("HTTP响应头: " + in.readLine());
    } catch (Exception e) {
      System.out.println("❌ 连接失败: " + e.getMessage());
    }
  }

  public static void printRoutingTable() throws SocketException {
    NetworkInterface.getNetworkInterfaces().asIterator()
        .forEachRemaining(ni -> {
          System.out.println("接口: " + ni.getName() + " - " + ni.getDisplayName());
          ni.getInterfaceAddresses().forEach(ia ->
              System.out.println("   IP: " + ia.getAddress().getHostAddress())
          );
        });
  }
}