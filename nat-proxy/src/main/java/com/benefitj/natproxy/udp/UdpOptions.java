package com.benefitj.natproxy.udp;

import com.benefitj.natproxy.ProxyOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * UDP配置
 */
@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class UdpOptions extends ProxyOptions<UdpOptions.SubOptions> {

  /**
   * 子配置
   */
  @SuperBuilder
  @NoArgsConstructor
  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class SubOptions extends ProxyOptions.Sub {
  }

}
