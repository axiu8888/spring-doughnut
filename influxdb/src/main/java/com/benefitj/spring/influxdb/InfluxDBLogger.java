package com.benefitj.spring.influxdb;

import com.benefitj.core.log.ILogger;
import com.benefitj.core.log.LoggerHolder;

public class InfluxDBLogger {

  static final LoggerHolder holder = new LoggerHolder(ILogger.get());

  public static LoggerHolder get() {
    return holder;
  }

  public static void set(ILogger log) {
    holder.setLog(log);
  }

}
