package com.benefitj.spring.dynamicdatasource.aop;

import com.benefitj.spring.dynamicdatasource.LookupKeyContext;

public class AopLookupKeyContext implements LookupKeyContext {

  /**
   * 缓存数据源类型
   */
  private final ThreadLocal<Object> local = new ThreadLocal<>();

  /**
   * 获取数据源
   */
  @Override
  public Object get() {
    return local.get();
  }

  /**
   * 设置数据源
   *
   * @param key
   */
  @Override
  public void set(Object key) {
    local.set(key);
  }

  /**
   * 移除数据源
   */
  @Override
  public void remove() {
    local.remove();
  }

  /**
   * 获取并设置新的key
   *
   * @param key 新的Key
   * @return 返回旧的key
   */
  @Override
  public Object getAndSet(Object key) {
    Object oldKey = local.get();
    if (key == null) {
      local.remove();
    } else {
      local.set(key);
    }
    return oldKey;
  }

}
