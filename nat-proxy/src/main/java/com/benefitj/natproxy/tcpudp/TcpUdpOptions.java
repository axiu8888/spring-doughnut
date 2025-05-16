package com.benefitj.natproxy.tcpudp;

import com.benefitj.natproxy.ProxyOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TCP配置
 */
@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class TcpUdpOptions extends ProxyOptions<TcpUdpOptions.SubOptions> {



  @SuperBuilder
  @NoArgsConstructor
  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class SubOptions extends ProxyOptions.Sub {

  }

}
