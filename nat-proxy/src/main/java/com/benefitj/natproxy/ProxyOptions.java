package com.benefitj.natproxy;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TCP配置
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class ProxyOptions {

  public static final String[] EMPTY = new String[0];

  /**
   * 是否启用
   */
  boolean enable = true;
  /**
   * 本地监听端口
   */
  Integer port;
  /**
   * 远程主机地址，比如：192.168.1.100:8080
   */
  String[] remotes = EMPTY;
  /**
   * 写入超时时间
   */
  Integer writerTimeout = 60;
  /**
   * 读取超时时间
   */
  Integer readerTimeout = 60;
  /**
   * 是否打印请求日志
   */
  boolean printRequest = false;
  /**
   * 是否打印响应日志
   */
  boolean printResponse = false;
  /**
   * 打印请求数据的长度
   */
  Integer printRequestSize = 30;
  /**
   * 打印响应数据的长度
   */
  Integer printResponseSize = 30;

  /**
   * 延迟结束，默认5秒
   */
  Integer delayExit = 5;

}
