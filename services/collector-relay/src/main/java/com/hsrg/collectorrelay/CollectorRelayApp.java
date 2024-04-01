package com.hsrg.collectorrelay;

import com.benefitj.core.EventLoop;
import com.benefitj.spring.listener.EnableAppStateListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableAppStateListener
@SpringBootApplication
public class CollectorRelayApp {
  public static void main(String[] args) {
    SpringApplication.run(CollectorRelayApp.class, args);
    EventLoop.main().execute(() -> {/* nothing do */});
  }
}
