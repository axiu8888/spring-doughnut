package com.benefitj.spring.influxdb.example;

import com.alibaba.fastjson.JSON;
import com.benefitj.core.EventLoop;
import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.influxdb.template.DefaultSubscriber;
import com.benefitj.spring.influxdb.template.RxJavaInfluxDBTemplate;
import com.benefitj.spring.influxdb.write.InfluxWriterManager;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;

@SpringBootTest
class InfluxDBApplicationTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private RxJavaInfluxDBTemplate template;

  @Autowired
  private InfluxWriterManager writerManager;

  private Random random = new Random();

  @Test
  void testWrite() {
    String line = generateLine();
    System.err.println(line);
    template.write(line);
  }

  private String generateLine() {
    TrendRates trendRates = new TrendRates();
    trendRates.setDeviceId("2333");
    trendRates.setTime(System.currentTimeMillis() / 1000);
    trendRates.setHeartRate((short) (50 + random.nextInt(90)));
    trendRates.setSpo2((byte) (90 + random.nextInt(10)));
    trendRates.setRespRate((short) random.nextInt(30));
    trendRates.setGesture(random.nextInt(20));
    trendRates.setEnergy(random.nextDouble());
    trendRates.setType("1");
    trendRates.setStep((short) random.nextInt(1000));
    return template.convert(trendRates).lineProtocol();
  }

  @Test
  void testQuery() {
    template.query("SELECT * FROM sys_trend_rates WHERE time > now() - 1d")
        .subscribe(new DefaultSubscriber<>() {
          @Override
          public void onNext(QueryResult queryResult) {
            List<TrendRates> trendRates = template.mapperTo(queryResult, TrendRates.class);
            System.err.println("data: " + JsonUtils.toJson(trendRates));
          }
        });
  }

  /**
   * ???????????????
   */
  @Test
  void testDropMeasurements() {
    QueryResult result = template.dropMeasurement("sys_trend_rates");
    System.err.println(JSON.toJSONString(result));
  }

  /**
   * ???????????????
   */
  @Test
  void testDropDB() {
    QueryResult result = template.dropDatabase();
    System.err.println(JSON.toJSONString(result));
  }

  @Test
  void testWriter() {
    for (int i = 0; i < 50; i++) {
      writerManager.write(generateLine(), "\n");
      EventLoop.sleepSecond(1);
    }
    EventLoop.io().execute(() -> writerManager.flush());
    EventLoop.sleepSecond(1);
  }

  @Test
  void testExport() {
    System.err.println("----------->:");
  }

}
