package com.benefitj.influxdb.template;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class SimpleSubscriber<T> implements Subscriber<T> {
  @Override
  public void onSubscribe(Subscription s) {
    s.request(Integer.MAX_VALUE);
  }

  @Override
  public abstract void onNext(T t);

  @Override
  public void onError(Throwable t) {
    t.printStackTrace();
  }

  @Override
  public void onComplete() {
    // ~
  }
}
