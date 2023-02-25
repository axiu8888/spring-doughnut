package com.benefitj.influxdb;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.file.IWriter;
import com.benefitj.influxdb.dto.QueryResult;
import com.benefitj.influxdb.template.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest(classes = App.class)
public class InfluxApiDBApiTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  InfluxTemplate template;

  private Random random = new Random();

  @BeforeEach
  public void setUp() {
  }

  @AfterEach
  public void tearDown() {
  }

  @Test
  void testQuery() {
    QueryResult result = template.postQuery("SHOW MEASUREMENTS ON test;");
    log.info("result ===>: {}", JSON.toJSONString(result));
  }


  @Test
  void testWrite() {
    String line = generateLine();
    log.info(line);
    template.write(line);
  }

  private String generateLine() {
    TrendRates tr = new TrendRates();
    tr.setDeviceId("2333");
    tr.setTime(System.currentTimeMillis() / 1000);
    tr.setHeartRate((short) (50 + random.nextInt(90)));
    tr.setSpo2((byte) (90 + random.nextInt(10)));
    tr.setRespRate((short) random.nextInt(30));
    tr.setGesture(random.nextInt(20));
    tr.setEnergy(random.nextDouble());
    tr.setType("1");
    tr.setStep((short) random.nextInt(1000));
    return template.lineProtocol(Collections.singletonList(tr));
  }

  @Test
  void testQuerySimple() {
    template.query("SELECT * FROM sys_trend_rates WHERE time > now() - 1d")
        .subscribe(new QuerySubscriber() {
          @Override
          public void onNext0(QueryResult qr) {
            List<TrendRates> trendRates = template.mapperTo(qr, TrendRates.class);
            log.info("result ===>: " + JSON.toJSONString(trendRates));
          }
        });
  }

  /**
   * 删除数据表
   */
  @Test
  void testDropMeasurements() {
    QueryResult result = template.dropMeasurement("sys_trend_rates");
    log.info(JSON.toJSONString(result));
  }

  @Test
  void testQueryChunkRaw() {
    template.query("SELECT * FROM hs_wave_package WHERE time >= 1d GROUP BY person_zid LIMIT 100;", 10)
        .subscribe(new QuerySubscriber() {
          @Override
          public void onNext0(@NotNull QueryResult result) {
            System.err.println(JSON.toJSONString(result));
          }
        });
  }

  @Test
  void testQueryChunk() {
    IWriter writer = IWriter.newFileWriter(IOUtils.createFile("D:/home/influx/" + IdUtils.uuid() + ".line"));
    template.query("SELECT * FROM hs_wave_package WHERE time >= 1d GROUP BY person_zid LIMIT 100;", 10)
        .subscribe(new QueryObserver() {
          @Override
          public void onSeriesStart(QueryResult.Series series, ValueConverter c) {
            //log.info("series ==>: {}", JSON.toJSONString(series));
            writer.write("series.start ==>: \n")
                .write(JSON.toJSONString(c.getColumns())).write("\n")
                .write(JSON.toJSONString(c.getTags()))
                .write("\n");
          }

          @Override
          public void onSeriesNext(List<Object> values, ValueConverter c, int pos) {
            writer.write(pos + ". time: ")
                .write(DateFmtter.fmtS(c.getTime()))
                .write(" ==>: ").write(JSON.toJSONString(values))
                .write("\n");
          }

          @Override
          public void onQueryComplete() {
            writer.flush().close();
          }
        });
  }

  @Test
  void testShowMeasurements() {
    List<String> measurements = template.getMeasurements();
    log.info("measurements: {}", measurements);
  }

  @Test
  void testShowTagAndValues() {
    List<String> tags = template.getTagKeys("sys_trend_rates");
    Map<String, List<String>> tagValues = new LinkedHashMap<>();
    tags.forEach(tag -> tagValues.put(tag, template.getTagValues("sys_trend_rates", tag)));
    log.info("tagValues: {}", tagValues);
  }

  @Test
  void testShowTagMap() {
    log.info("getTagValuesMap: {}", template.getTagValuesMap("sys_trend_rates"));
  }
}
