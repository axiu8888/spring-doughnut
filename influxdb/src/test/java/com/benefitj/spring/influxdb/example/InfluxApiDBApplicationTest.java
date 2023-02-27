package com.benefitj.spring.influxdb.example;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.EventLoop;
import com.benefitj.spring.influxdb.spring.EnableInfluxWriterManager;
import com.benefitj.spring.influxdb.spring.EnableRxJavaInfluxDB;
import com.benefitj.spring.influxdb.template.DefaultSubscriber;
import com.benefitj.spring.influxdb.template.RxJavaInfluxDBTemplate;
import com.benefitj.spring.influxdb.write.InfluxWriterManager;
import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.annotation.TimeColumn;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@EnableRxJavaInfluxDB
@EnableInfluxWriterManager
@SpringBootTest
class InfluxApiDBApplicationTest {

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
        .subscribe(new DefaultSubscriber<QueryResult>() {
          @Override
          public void onNext(QueryResult queryResult) {
            List<TrendRates> trendRates = template.mapperTo(queryResult, TrendRates.class);
            System.err.println("data: " + JSON.toJSONString(trendRates));
          }
        });
  }

  @Test
  void testQueryString() {
    template.queryString(new Query("SELECT * FROM hs_wave_package WHERE time >= 1d GROUP BY person_zid LIMIT 100;", template.getDatabase()), 10)
        .subscribe(lines -> System.err.println(lines));
  }

  /**
   * 删除数据表
   */
  @Test
  void testDropMeasurements() {
    QueryResult result = template.dropMeasurement("sys_trend_rates");
    System.err.println(JSON.toJSONString(result));
  }


  /**
   * 查询
   */
  @Test
  void testGetMeasurements() {
    List<String> measurements = template.getMeasurements();
    System.err.println("measurements ==>: " + measurements);
  }

  /**
   * 删除数据库
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


  /**
   */
  @Data
  @Measurement(name = "sys_trend_rates", timeUnit = TimeUnit.SECONDS)
  public class TrendRates {
    /**
     * 时间戳
     */
    @TimeColumn
    @Column(name = "time")
    private Long time;
    /**
     * 设备ID
     *
     * 通过重新定义字段，覆盖父类的字段，让 device_id 作为tag
     */
    @Column(name = "deviceId", tag = true)
    private String deviceId;
    /**
     * 心率
     */
    @Column(name = "heartRate")
    private Short heartRate;
    /**
     * 呼吸率
     */
    @Column(name = "respRate")
    private Short respRate;
    /**
     * 血氧
     */
    @Column(name = "spo2")
    private Byte spo2;
    /**
     * 体位
     */
    @Column(name = "gesture")
    private Integer gesture;
    /**
     * 能耗, 卡路里
     */
    @Column(name = "energy")
    private Double energy;
    /**
     * 步数
     */
    @Column(name = "step")
    private Short step;
    /**
     * 默认秒；允许值：ss mm
     */
    @Column(name = "type")
    private String type;

  }

}
