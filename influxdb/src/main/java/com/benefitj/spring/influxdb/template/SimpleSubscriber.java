package com.benefitj.spring.influxdb.template;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.Consumer;

public abstract class SimpleSubscriber<T> implements Subscriber<T> {

  public Subscription subscription;

  @Override
  public void onSubscribe(Subscription s) {
    this.subscription = s;
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

  public void cancel() {
    Subscription s = subscription;
    if (s != null) {
      s.cancel();
    }
  }

  public static <T> SimpleSubscriber<T> create(Consumer<T> onNext) {
    return create(s -> {/*__*/}, onNext, Throwable::printStackTrace);
  }

  public static <T> SimpleSubscriber<T> create(Consumer<T> onNext,
                                               Consumer<Throwable> onError) {
    return create(s -> {/*__*/}, onNext, onError);
  }

  public static <T> SimpleSubscriber<T> create(Consumer<Subscription> onStart,
                                               Consumer<T> onNext,
                                               Consumer<Throwable> onError) {
    return new SimpleSubscriber<T>() {

      @Override
      public void onSubscribe(Subscription s) {
        super.onSubscribe(s);
        if (onStart != null) {
          onStart.accept(s);
        }
      }

      @Override
      public void onNext(T t) {
        onNext.accept(t);
      }

      @Override
      public void onError(Throwable t) {
        if (onError != null) {
          onError.accept(t);
        } else {
          t.printStackTrace();
        }
      }
    };
  }
}
