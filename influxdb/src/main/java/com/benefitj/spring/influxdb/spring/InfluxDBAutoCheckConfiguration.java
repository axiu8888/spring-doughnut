package com.benefitj.spring.influxdb.spring;

import com.benefitj.spring.influxdb.write.InfluxWriteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * InfluxDB自动检查写入
 */
@EnableAsync
@EnableScheduling
@Configuration
public class InfluxDBAutoCheckConfiguration {

  @Autowired
  private InfluxWriteManager manager;

  /**
   * 异步检查
   */
  @Async
  @Scheduled(fixedRate = 1000)
  public void autoCheck() {
    manager.checkAndFlush();
  }

}
