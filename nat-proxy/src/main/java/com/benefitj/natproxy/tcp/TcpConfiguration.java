package com.benefitj.natproxy.tcp;

import com.benefitj.spring.listener.AppStateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * TCP代理配置
 */
@ConditionalOnProperty(prefix = "tcp", value = "enable", matchIfMissing = false)
@Configuration
public class TcpConfiguration {

  /**
   * 配置
   */
  @ConditionalOnMissingBean
  @Bean
  @ConfigurationProperties(prefix = "tcp")
  public TcpOptions tcpOptions() {
    return new TcpOptions();
  }

  /**
   * 开关
   */
  @ConditionalOnMissingBean
  @Bean
  public TcpProxySwitcher tcpProxySwitcher(TcpOptions options) {
    return new TcpProxySwitcher(options);
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
