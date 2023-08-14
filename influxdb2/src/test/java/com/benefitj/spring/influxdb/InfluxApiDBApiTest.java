package com.benefitj.spring.influxdb;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.IOUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.file.IWriter;
import com.benefitj.http.ProgressListener;
import com.benefitj.http.ProgressRequestBody;
import com.benefitj.spring.influxdb.dto.FieldKey;
import com.benefitj.spring.influxdb.dto.QueryResult;
import com.benefitj.spring.influxdb.spring.InfluxConfiguration;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import com.benefitj.spring.influxdb.template.QueryObserver;
import com.benefitj.spring.influxdb.template.QuerySubscriber;
import com.benefitj.spring.influxdb.template.ValueConverter;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@SpringBootTest(classes = InfluxConfiguration.class)
@Slf4j
public class InfluxApiDBApiTest {

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
    IWriter writer = IWriter.newFileWriter("D:/tmp/influxdb/" + IdUtils.uuid() + ".line");
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

  @Test
  void testMeasurementInfo() {
    Map<String, FieldKey> fieldKeyMap = template.getFieldKeyMap(template.getDatabase(), template.getRetentionPolicy(), "hs_alarm", true);
    log.info("fieldKeyMap: \n{}", JSON.toJSONString(fieldKeyMap, JSONWriter.Feature.PrettyFormat));
  }

  /**
   * 导出 line 文件
   */
  @Test
  void test_exportLines() {
    String database = template.getDatabase();
    String retentionPolicy = template.getRetentionPolicy();
    List<MeasurementInfo> measurementInfos = template.getMeasurements()
        .stream()
        .filter(name -> !name.equalsIgnoreCase("hs_wave_point")) // 不保存波形趋势
        .map(name -> MeasurementInfo.builder()
            .name(name)
            .fieldKeyMap(template.getFieldKeyMap(database, retentionPolicy, name, true))
            .build())
        .collect(Collectors.toList());

    File dir = IOUtils.createFile("D:/tmp/influxdb", true);

    Long startTime = DateFmtter.parseToLong("2023-08-11 00:00:00");
    Long endTime = DateFmtter.parseToLong("2023-08-13 23:59:59");
    String condition = " AND person_zid = '1a7f8fa0df8e4049b415dcf6e77f3dc0' ";
//    String condition = "";

    //log.info("measurementInfos ==>: \n{}", JSON.toJSONString(measurementInfos, JSONWriter.Feature.PrettyFormat));
    for (MeasurementInfo measurementInfo : measurementInfos) {
      File line = IOUtils.createFile(dir, measurementInfo.name + ".line");
      template.export(line, measurementInfo.name, 5000, startTime, endTime, condition);
      if (line.length() <= 0) {
        line.delete(); // 没有数据，删除空文件
      }
    }
  }

  /**
   * 导入 line 文件
   */
  @Test
  void test_loadLines() {
    File dir = new File("D:/tmp/influxdb");
    File[] lines = dir.listFiles(pathname -> pathname.getName().endsWith(".line") && pathname.length() > 0);
    assert lines != null;
    for (File line : lines) {
      log.info("upload file: {}", line);
      AtomicLong prev = new AtomicLong(0);
      AtomicLong current = new AtomicLong(0);
      ProgressRequestBody body = new ProgressRequestBody(RequestBody.create(line, InfluxTemplate.MEDIA_TYPE_STRING), new ProgressListener() {
        @Override
        public void onProgressChange(long totalLength, long progress, boolean done) {
          log.info("file[{}], totalLength: {}, progress: {}, done: {}", line, totalLength, progress, done);
          prev.set(current.get());
          current.set(progress);
        }
      });
      template.write(body);
    }
  }

  @SuperBuilder
  @Data
  public static class MeasurementInfo {

    String name;

    Map<String, FieldKey> fieldKeyMap;
  }

}
