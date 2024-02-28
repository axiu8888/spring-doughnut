package com.benefitj.spring.listener;


import org.springframework.util.function.SingletonSupplier;

import java.util.*;

/**
 * APP状态注册器
 */
public final class AppStateHook {

  static final SingletonSupplier<AppStateHook> singleton = SingletonSupplier.of(AppStateHook::new);

  public static AppStateHook get() {
    return singleton.get();
  }

  /**
   * 注册监听
   *
   * @param listener 监听
   */
  public static void register(AppStateListener listener) {
    get().put(listener, listener);
  }

  /**
   * 注册监听
   *
   * @param start 启动监听
   * @param stop  停止监听
   */
  public static void register(AppStartListener start, AppStopListener stop) {
    register(new AppStateListenerWrapper(start, stop));
  }

  /**
   * 注册监听
   *
   * @param listener 监听
   */
  public static void registerStart(AppStartListener listener) {
    get().put(listener, new AppStateListenerWrapper(listener));
  }

  /**
   * 注册监听
   *
   * @param listener 监听
   */
  public static void registerStop(AppStopListener listener) {
    get().put(listener, new AppStateListenerWrapper(listener));
  }

  /**
   * 取消注册监听
   *
   * @param listener 监听
   */
  public static void unregister(Object listener) {
    get().remove(listener);
  }

  private final Map<Object, AppStateListener> map = Collections.synchronizedMap(new LinkedHashMap<>());

  public Map<Object, AppStateListener> getMap() {
    return map;
  }

  public List<AppStateListener> listeners() {
    if (getMap().isEmpty()) {
      return Collections.emptyList();
    }
    List<AppStateListener> listeners = new ArrayList<>(getMap().size());
    getMap().forEach((o, listener) -> listeners.add(listener));
    return listeners;
  }

  /**
   * 注册监听
   *
   * @param listener 监听
   */
  public void put(Object key, AppStateListener listener) {
    getMap().putIfAbsent(key, listener);
  }

  /**
   * 取消注册监听
   *
   * @param key 监听
   */
  public void remove(Object key) {
    getMap().remove(key);
  }

}
