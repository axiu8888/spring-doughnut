package com.benefitj.natproxy.udptcp;

import com.benefitj.spring.listener.AppStateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * UDP-TCP代理配置
 */
@Configuration
public class UdpTcpConfiguration {

  /**
   * 配置
   */
  @ConditionalOnBean
  @Bean
  @ConfigurationProperties(prefix = "udp-tcp")
  public UdpTcpOptions udpTcpOptions() {
    return new UdpTcpOptions();
  }

  /**
   * UDP-TCP服务端
   */
  @ConditionalOnBean
  @Bean
  public UdpTcpProxyServer udpTcpProxyServer(UdpTcpOptions options) {
    return new UdpTcpProxyServer(options);
  }

  /**
   * 开关
   */
  @ConditionalOnBean
  @Bean
  public UdpTcpProxySwitcher udpTcpProxySwitcher(UdpTcpOptions options, UdpTcpProxyServer server) {
    return new UdpTcpProxySwitcher(options, server);
  }

  /**
   * UDP代理启动和停止
   */
  @Lazy(value = false)
  @ConditionalOnMissingBean(name = "udpTcpProxyListener")
  @Bean(name = "udpTcpProxyListener")
  public AppStateListener udpTcpProxyListener(UdpTcpProxySwitcher switcher) {
    return AppStateListener.create(
        evt -> switcher.startServer(),
        evt -> switcher.stopServer()
    );
  }

}
