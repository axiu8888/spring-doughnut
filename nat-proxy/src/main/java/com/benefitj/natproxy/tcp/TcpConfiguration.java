package com.benefitj.natproxy.tcp;

import com.benefitj.spring.listener.AppStateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * TCP代理配置
 */
@Configuration
public class TcpConfiguration {

  /**
   * 配置
   */
  @ConditionalOnBean
  @Bean
  @ConfigurationProperties(prefix = "tcp")
  public TcpOptions tcpOptions() {
    return new TcpOptions();
  }

  /**
   * TCP服务端
   */
  @ConditionalOnBean
  @Bean
  public TcpProxyServer tcpProxyServer(TcpOptions options) {
    return new TcpProxyServer(options);
  }

  /**
   * 开关
   */
  @ConditionalOnBean
  @Bean
  public TcpProxySwitcher tcpProxySwitcher(TcpOptions options, TcpProxyServer server) {
    return new TcpProxySwitcher(options, server);
  }

  /**
   * TCP代理启动和停止
   */
  @Lazy(value = false)
  @ConditionalOnMissingBean(name = "tcpProxyListener")
  @Bean(name = "tcpProxyListener")
  public AppStateListener tcpProxyListener(TcpProxySwitcher switcher) {
    return AppStateListener.create(
        evt -> switcher.startServer(),
        evt -> switcher.stopServer()
    );
  }
}
