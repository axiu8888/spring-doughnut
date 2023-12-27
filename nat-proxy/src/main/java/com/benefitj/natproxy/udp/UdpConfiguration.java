package com.benefitj.natproxy.udp;

import com.benefitj.spring.listener.AppStateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * UDP代理配置
 */
@Lazy(value = false)
@Configuration
public class UdpConfiguration {

  /**
   * 配置
   */
  @ConditionalOnBean
  @Bean
  @ConfigurationProperties(prefix = "udp")
  public UdpOptions udpOptions() {
    return new UdpOptions();
  }

  /**
   * UDP服务端
   */
  @ConditionalOnBean
  @Bean
  public UdpProxyServer udpProxyServer(UdpOptions options) {
    return new UdpProxyServer(options);
  }

  /**
   * 开关
   */
  @ConditionalOnBean
  @Bean
  public UdpProxySwitcher udpProxySwitcher(UdpOptions options, UdpProxyServer server) {
    return new UdpProxySwitcher(options, server);
  }

  /**
   * UDP代理启动和停止
   */
  @Lazy(value = false)
  @ConditionalOnMissingBean(name = "udpProxyListener")
  @Bean(name = "udpProxyListener")
  public AppStateListener udpProxyListener(UdpProxySwitcher switcher) {
    return AppStateListener.create(
        evt -> switcher.startServer(),
        evt -> switcher.stopServer()
    );
  }

}
