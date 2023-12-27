package com.benefitj.natproxy.udptcp;

import com.benefitj.natproxy.ProxyOptions;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * UDP配置
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class UdpTcpOptions extends ProxyOptions {
  /**
   * 是否自动重连，对于部分连接，重连可能会导致错误
   */
  @Builder.Default
  Boolean autoReconnect = false;
  /**
   * 自动重连的时间
   */
  @Builder.Default
  Integer reconnectDelay = 3;
}
