package com.benefitj.spring.quartz.worker;

import com.benefitj.core.SingletonSupplier;
import com.benefitj.core.functions.WrappedMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Job管理
 */
public class QuartzWorkerManager implements WrappedMap<String, QuartzWorkerInvoker> {

  static final SingletonSupplier<QuartzWorkerManager> singleton = SingletonSupplier.of(QuartzWorkerManager::new);

  public static QuartzWorkerManager get() {
    return singleton.get();
  }

  final Map<String, QuartzWorkerInvoker> _internal = new ConcurrentHashMap<>();

  private QuartzWorkerManager() {
  }

  @Override
  public Map<String, QuartzWorkerInvoker> map() {
    return _internal;
  }

}
