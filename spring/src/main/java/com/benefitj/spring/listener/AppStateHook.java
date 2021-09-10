package com.benefitj.spring.listener;


import java.util.*;

/**
 * APP状态注册器
 */
public final class AppStateHook {

  public static AppStateHook getInstance() {
    return Holder.INSTANCE;
  }

  /**
   * 注册监听
   *
   * @param listener 监听
   */
  public static void register(AppStateListener listener) {
    getInstance().put(listener, listener);
  }

  /**
   * 注册监听
   *
   * @param listener 监听
   */
  public static void registerStart(AppStartListener listener) {
    getInstance().put(listener, new AppStateListenerWrapper(listener));
  }

  /**
   * 注册监听
   *
   * @param listener 监听
   */
  public static void registerStop(AppStopListener listener) {
    getInstance().put(listener, new AppStateListenerWrapper(listener));
  }

  /**
   * 取消注册监听
   *
   * @param listener 监听
   */
  public static void unregister(Object listener) {
    getInstance().remove(listener);
  }

  static class Holder {
    private static final AppStateHook INSTANCE;
    static {
      INSTANCE = new AppStateHook();
    }
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
