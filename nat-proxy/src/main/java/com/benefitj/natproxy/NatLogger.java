package com.benefitj.natproxy;

import com.benefitj.core.log.ILogger;
import com.benefitj.core.log.LoggerHolder;

public class NatLogger {

  static final LoggerHolder holder = new LoggerHolder(ILogger.get());

  public static ILogger get() {
    return holder;
  }

  public static void set(ILogger log) {
    holder.setLog(log);
  }

}
