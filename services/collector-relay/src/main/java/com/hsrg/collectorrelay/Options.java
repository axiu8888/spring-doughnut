package com.hsrg.collectorrelay;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@SuperBuilder
@NoArgsConstructor
@Data
@ConfigurationProperties(prefix = "relay")
@Component
public class Options {
  /**
   * 远程服务器地址，如：192.168.1.198:62014
   */
  String remote;
  /**
   * CHE目录，如：./CHE
   */
  String cheDir;
  /**
   * 一次性发送完，不使用调度
   */
  @Builder.Default
  boolean useSchedule = false;
  /**
   * 多久执行一次
   */
  @Builder.Default
  int delay = 1;
  /**
   * 时间单位，默认毫秒
   */
  @Builder.Default
  TimeUnit unit = TimeUnit.MILLISECONDS;
}

