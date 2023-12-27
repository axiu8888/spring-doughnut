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
@EqualsAndHashCode(callSuper = true)
@Data
public class TcpUdpOptions extends ProxyOptions {
}
