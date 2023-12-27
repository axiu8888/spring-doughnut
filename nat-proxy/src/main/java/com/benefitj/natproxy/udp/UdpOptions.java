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
@EqualsAndHashCode(callSuper = true)
@Data
public class UdpOptions extends ProxyOptions {
}
