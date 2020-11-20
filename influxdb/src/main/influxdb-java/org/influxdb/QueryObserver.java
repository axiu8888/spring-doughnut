package org.influxdb;

/**
 * 查询的观察者
 *
 * @param <T>
 */
public interface QueryObserver<T> {
  /**
   * next
   */
  void onNext(T t);

  /**
   * throw error
   */
  void onError(Throwable e);

  /**
   * completed
   */
  void onComplete();
}