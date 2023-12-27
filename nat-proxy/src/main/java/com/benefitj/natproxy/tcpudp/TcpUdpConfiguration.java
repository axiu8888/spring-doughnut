package com.benefitj.natproxy.tcpudp;

import com.benefitj.spring.listener.AppStateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * TCP转UDP代理配置
 */
@Configuration
public class TcpUdpConfiguration {

  /**
   * 配置
   */
  @ConditionalOnBean
  @Bean
  @ConfigurationProperties(prefix = "tcp-udp")
  public TcpUdpOptions tcpUdpOptions() {
    return new TcpUdpOptions();
  }

  /**
   * TCP-UDP服务端
   */
  @ConditionalOnBean
  @Bean
  public TcpUdpProxyServer tcpUdpProxyServer(TcpUdpOptions options) {
    return new TcpUdpProxyServer(options);
  }

  /**
   * 开关
   */
  @ConditionalOnBean
  @Bean
  public TcpUdpProxySwitcher tcpProxySwitcher(TcpUdpOptions options, TcpUdpProxyServer server) {
    return new TcpUdpProxySwitcher(options, server);
  }

  /**
   * TCP-UDP代理启动和停止
   */
  @Lazy(value = false)
  @ConditionalOnMissingBean(name = "tcpUdpProxyListener")
  @Bean(name = "tcpUdpProxyListener")
  public AppStateListener tcpUdpProxyListener(TcpUdpProxySwitcher switcher) {
    return AppStateListener.create(
        evt -> switcher.startServer(),
        evt -> switcher.stopServer()
    );
  }
}
