package com.benefitj.natproxy;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TCP配置
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class ProxyOptions<T> {

  public static final String[] EMPTY = new String[0];

  /**
   * 是否启用
   */
  @Builder.Default
  boolean enable = true;

  /**
   * 代理
   */
  T[] proxy;

  @SuperBuilder
  @NoArgsConstructor
  @Data
  public static class Sub {
    /**
     * 本地监听端口
     */
    Integer port;
    /**
     * 远程主机地址，比如：192.168.1.100:8080
     */
    @Builder.Default
    String[] remotes = EMPTY;
    /**
     * 写入超时时间
     */
    @Builder.Default
    Integer writerTimeout = 60;
    /**
     * 读取超时时间
     */
    @Builder.Default
    Integer readerTimeout = 60;
    /**
     * 是否打印请求日志
     */
    @Builder.Default
    boolean printRequest = false;
    /**
     * 是否打印响应日志
     */
    @Builder.Default
    boolean printResponse = false;
    /**
     * 打印请求数据的长度
     */
    @Builder.Default
    Integer printRequestSize = 30;
    /**
     * 打印响应数据的长度
     */
    @Builder.Default
    Integer printResponseSize = 30;

    /**
     * 延迟结束，默认5秒
     */
    @Builder.Default
    Integer delayExit = 5;
  }

}
