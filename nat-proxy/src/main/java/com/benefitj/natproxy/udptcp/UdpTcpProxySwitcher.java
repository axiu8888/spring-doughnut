package com.benefitj.natproxy.udptcp;

import com.benefitj.core.EventLoop;
import com.benefitj.core.log.ILogger;
import com.benefitj.natproxy.NatLogger;
import com.benefitj.natproxy.ProxySwitcher;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UdpTcpProxySwitcher implements ProxySwitcher {

  final ILogger log = NatLogger.get();

  final List<UdpTcpProxyServer> servers = new LinkedList<>();
  final UdpTcpOptions options;

  public UdpTcpProxySwitcher(UdpTcpOptions options) {
    this.options = options;
  }

  @Override
  public void startServer() {
    try {
      if (options.isEnable()) return;

      for (UdpTcpOptions.SubOptions so : options.getProxy()) {
        Integer port = so.getPort();
        if (port == null) {
          throw new IllegalStateException("本地监听端口不能为空!");
        }

        String[] remotes = so.getRemotes();
        if (remotes == null || remotes.length < 1) {
          throw new IllegalStateException("远程主机地址不能为空!");
        }

        UdpTcpProxyServer server = new UdpTcpProxyServer(so);
        server.localAddress(port);
        server.start(f ->
            log.info("udp-tcp proxy started, local port: {}, remotes: {}, success: {}"
                , so.getPort()
                , Arrays.toString(remotes)
                , f.isSuccess()
            )
        );
        servers.add(server);
      }
    } catch (Exception e) {
      log.error("throws: " + e.getMessage(), e);
      // 5秒后停止
      EventLoop.single().schedule(() ->
          System.exit(0), 5, TimeUnit.SECONDS);
    }
  }

  @Override
  public void stopServer() {
    try {
      if (options.isEnable()) {
        return;
      }
      for (UdpTcpProxyServer server : servers) {
        try {
          server.stop(f ->
              log.info("udp-tcp proxy stopped, local port: {}, remotes: {}, success: {}"
                  , server.getOptions().getPort()
                  , Arrays.toString(server.getOptions().getRemotes())
                  , f.isSuccess()
              )
          );
        } catch (Exception e) {
          log.error("UDP->TCP[" + server.getOptions().getPort() + "] throws: " + e.getMessage(), e);
        }
      }
    } catch (Exception e) {
      log.error("throws: " + e.getMessage(), e);
      System.exit(0);
    }
  }
}

