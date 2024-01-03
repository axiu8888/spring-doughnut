package com.benefitj.natproxy.tcp;

import com.benefitj.natproxy.ProxyOptions;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TCP配置
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class TcpOptions extends ProxyOptions {
  /**
   * 是否自动重连，对于部分连接，重连可能会导致错误
   */
  @Builder.Default
  boolean autoReconnect = false;
  /**
   * 自动重连的时间
   */
  @Builder.Default
  Integer reconnectDelay = 3;
  /**
   * 是否快速失败
   */
  @Builder.Default
  boolean fastFailover = false;

}
