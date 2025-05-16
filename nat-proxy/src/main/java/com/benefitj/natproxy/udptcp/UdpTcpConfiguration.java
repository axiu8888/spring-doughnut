package com.benefitj.natproxy.udptcp;

import com.benefitj.spring.listener.AppStateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * UDP-TCP代理配置
 */
@ConditionalOnProperty(prefix = "udp-tcp", value = "enable", matchIfMissing = false)
@Configuration
public class UdpTcpConfiguration {

  /**
   * 配置
   */
  @ConditionalOnMissingBean
  @Bean
  @ConfigurationProperties(prefix = "udp-tcp")
  public UdpTcpOptions udpTcpOptions() {
    return new UdpTcpOptions();
  }

  /**
   * 开关
   */
  @ConditionalOnMissingBean
  @Bean
  public UdpTcpProxySwitcher udpTcpProxySwitcher(UdpTcpOptions options) {
    return new UdpTcpProxySwitcher(options);
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
