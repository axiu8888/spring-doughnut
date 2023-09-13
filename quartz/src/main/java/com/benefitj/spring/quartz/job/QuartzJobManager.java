package com.benefitj.spring.quartz.job;

import com.benefitj.core.functions.WrappedMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuartzJobManager implements WrappedMap<String, QuartzJobInvoker> {

  final Map<String, QuartzJobInvoker> invokers = new ConcurrentHashMap<>();

  @Override
  public Map<String, QuartzJobInvoker> getOriginal() {
    return invokers;
  }

}
