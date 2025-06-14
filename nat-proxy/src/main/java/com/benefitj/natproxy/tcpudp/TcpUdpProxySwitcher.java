package com.benefitj.natproxy.tcpudp;

import com.benefitj.core.EventLoop;
import com.benefitj.core.log.ILogger;
import com.benefitj.natproxy.NatLogger;
import com.benefitj.natproxy.ProxySwitcher;
import com.benefitj.spring.listener.OnAppStart;
import com.benefitj.spring.listener.OnAppStop;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TcpUdpProxySwitcher implements ProxySwitcher {

  final ILogger log = NatLogger.get();

  final List<TcpUdpProxyServer> servers = new LinkedList<>();
  final TcpUdpOptions options;

  public TcpUdpProxySwitcher(TcpUdpOptions options) {
    this.options = options;
  }

  @OnAppStart
  @Override
  public void startServer() {
    try {
      if (!options.isEnable()) return;

      for (TcpUdpOptions.SubOptions so : options.getProxy()) {
        Integer port = so.getPort();
        if (port == null) {
          throw new IllegalStateException("本地监听端口不能为空!");
        }

        String[] remotes = so.getRemotes();
        if (remotes == null || remotes.length < 1) {
          throw new IllegalStateException("远程主机地址不能为空!");
        }

        TcpUdpProxyServer server = new TcpUdpProxyServer(so);
        server.localAddress(port);
        server.start(f ->
            log.info("[tcp-udp] proxy started, local port: {}, remotes: {}, success: {}"
                , so.getPort()
                , Arrays.toString(remotes)
                , f.isSuccess()
            )
        );
        this.servers.add(server);
      }
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
      for (TcpUdpProxyServer server : servers) {
        try {
          server.stop(f ->
              log.info("[tcp-udp] proxy stopped, local port: {}, remotes: {}, success: {}"
                  , server.getOptions().getPort()
                  , Arrays.toString(server.getOptions().getRemotes())
                  , f.isSuccess()
              )
          );
        } catch (Exception e) {
          log.error("TCP -> UDP [" + server.getOptions().getPort() + "] throws: " + e.getMessage(), e);
        }
      }
    } catch (Exception e) {
      log.error("throws: " + e.getMessage(), e);
      System.exit(0);
    }
  }
}

