package com.benefitj.natproxy.tcpudp;

import com.benefitj.core.EventLoop;
import com.benefitj.natproxy.ProxySwitcher;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.listener.OnAppStop;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpUdpProxySwitcher implements ProxySwitcher {

   TcpUdpOptions options;
   TcpUdpProxyServer server;



  public TcpUdpProxySwitcher(TcpUdpOptions options, TcpUdpProxyServer server) {
    this.options = options;
    this.server = server;
  }

  @OnAppStart
  @Override
  public void startServer() {
    try {
      if (!options.isEnable()) {
        return;
      }
      Integer port = options.getPort();
      if (port == null) {
        throw new IllegalStateException("本地监听端口不能为空!");
      }

      String[] remotes = options.getRemotes();
      if (remotes == null || remotes.length < 1) {
        throw new IllegalStateException("远程主机地址不能为空!");
      }

      server.localAddress(port);
      server.start(f ->
          log.info("[tcp-udp] proxy started, local port: {}, remotes: {}, success: {}"
              , options.getPort()
              , Arrays.toString(remotes)
              , f.isSuccess()
          )
      );
    } catch (Exception e) {
      log.error("throws: " + e.getMessage(), e);
      // 5秒后停止
      EventLoop.single().schedule(() ->
          System.exit(0), 5, TimeUnit.SECONDS);
    }
  }

  @OnAppStop
  @Override
  public void stopServer() {
    try {
      if (!options.isEnable()) {
        return;
      }
      server.stop(f ->
          log.info("[tcp-udp] proxy stopped, local port: {}, remotes: {}, success: {}"
              , options.getPort()
              , Arrays.toString(options.getRemotes())
              , f.isSuccess()
          )
      );
    } catch (Exception e) {
      log.error("throws: " + e.getMessage(), e);
      System.exit(0);
    }
  }
}

