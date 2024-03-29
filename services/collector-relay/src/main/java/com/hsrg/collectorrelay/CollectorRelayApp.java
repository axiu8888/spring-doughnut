package com.hsrg.collectorrelay;

import com.benefitj.core.EventLoop;
import com.benefitj.spring.listener.EnableAppStateListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.stream.Stream;


@EnableAppStateListener
@SpringBootApplication
public class CollectorRelayApp {
  public static void main(String[] args) {
    SpringApplication.run(CollectorRelayApp.class, args);
    EventLoop.main().execute(() -> {/* nothing done */});
  }

  private static void changeName(File dir) {
    File[] files = dir.listFiles();
    if (files != null && files.length > 0) {
      Stream.of(files)
          .filter(f -> f.getName().endsWith(".HEX"))
          .forEach(f -> {
            f.renameTo(new File(f.getParentFile(), f.getName().replace(".HEX", ".CHE")));
          });
    }
  }

}
