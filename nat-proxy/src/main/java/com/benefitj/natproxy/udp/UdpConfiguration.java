package com.benefitj.natproxy.udp;

import com.benefitj.spring.listener.AppStateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * UDP代理配置
 */
@ConditionalOnProperty(prefix = "udp", value = "enable", matchIfMissing = false)
@Configuration
public class UdpConfiguration {

  /**
   * 配置
   */
  @ConfigurationProperties(prefix = "udp")
  @ConditionalOnMissingBean
  @Bean
  public UdpOptions udpOptions() {
    return new UdpOptions();
  }

  /**
   * UDP服务端
   */
  @ConditionalOnMissingBean
  @Bean
  public UdpProxyServer udpProxyServer(UdpOptions options) {
    return new UdpProxyServer(options);
  }

  /**
   * 开关
   */
  @ConditionalOnMissingBean
  @Bean
  public UdpProxySwitcher udpProxySwitcher(UdpOptions options, UdpProxyServer server) {
    return new UdpProxySwitcher(options, server);
  }

  /**
   * UDP代理启动和停止
   */
  @ConditionalOnMissingBean(name = "udpProxyListener")
  @Bean(name = "udpProxyListener")
  public AppStateListener udpProxyListener(UdpProxySwitcher switcher) {
    return AppStateListener.create(
        evt -> switcher.startServer(),
        evt -> switcher.stopServer()
    );
  }

}
