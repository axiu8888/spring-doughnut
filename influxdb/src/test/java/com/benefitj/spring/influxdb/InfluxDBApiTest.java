package com.benefitj.spring.influxdb;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.benefitj.core.*;
import com.benefitj.core.file.FileWriterImpl;
import com.benefitj.core.file.IWriter;
import com.benefitj.core.log.Slf4jLevel;
import com.benefitj.core.log.Slf4jLogger;
import com.benefitj.http.ProgressRequestBody;
import com.benefitj.javastruct.JavaStructManager;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.*;
import com.benefitj.spring.influxdb.pojo.InfluxWavePackage;
import com.benefitj.spring.influxdb.pojo.SleepPacket;
import com.benefitj.spring.influxdb.spring.InfluxConfiguration;
import com.benefitj.spring.influxdb.spring.InfluxWriteManagerConfiguration;
import com.benefitj.spring.influxdb.template.*;
import com.benefitj.spring.influxdb.write.InfluxWriteManager;
import com.squareup.moshi.Moshi;
import io.reactivex.rxjava3.core.Flowable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(classes = {
    InfluxConfiguration.class,
    InfluxWriteManagerConfiguration.class
})
@Slf4j
class InfluxDBApiTest {

  @Autowired
  InfluxTemplate template;

  @Autowired
  InfluxOptions options;

  @Autowired
  InfluxApiFactory factory;

  @Autowired
  PointConverterFactory converterFactory;

  @Autowired
  InfluxWriteManager writeManager;

  private Random random = new Random();

  @BeforeEach
  public void setUp() {
    InfluxDBLogger.set(Slf4jLogger.newProxy(log, Slf4jLevel.INFO));
  }

  @AfterEach
  public void tearDown() {
  }

  @Test
  void test_showMeasurements() {
    QueryResult result = template.postQuery(template.getDatabase(), "show measurements;");
    log.info("{}", JSON.toJSONString(result));
  }

  @Test
  void test_createSubscriptions() {
    // CREATE SUBSCRIPTION "sub0" ON "mydb"."autogen" DESTINATIONS ALL 'http://www.example.com:8086', 'http://www.example.com:8087'
    // CREATE SUBSCRIPTION "sub0" ON "mydb"."autogen" DESTINATIONS ANY 'udp://www.example.com:9090', 'udp://www.example.com:9090'
    //QueryResult result = template.createSubscription("backup", "hsrg", "autogen", false, "http://hsrg-influx2:8086");
    QueryResult result = template.createSubscription("backup", false, "http://hsrg-influx2:8086");
    //QueryResult result = template.postQuery("CREATE SUBSCRIPTION \"backup\" ON \"hsrg\".\"autogen\" DESTINATIONS ALL 'http://hsrg-influx2:8086'");
    log.info("result ===>: {}", JSON.toJSONString(result));
  }

  @Test
  void test_showSubscriptions() {
    List<Subscription> result = template.showSubscriptions();
    log.info("result ===>: {}", JSON.toJSONString(result));
  }

  @Test
  void test_dropSubscriptions() {
    QueryResult result = template.dropSubscription("backup", "hsrg", "autogen");
    log.info("result ===>: {}", JSON.toJSONString(result));
  }

  @Test
  void testCreateCQ() {
    boolean result = template.createCQIfNotExist(template.getDatabase(), "cq_measure_hours",
        "SELECT " +
            "first(package_sn) AS first_sn" +
            ", last(package_sn) AS last_sn" +
            ", DATE_FORMAT(time, '%Y-%m-%d') AS day" +
//            ", DATE_FORMAT(time, '%H') AS hour " +
            "\nINTO \"autogen\".\"hs_measure_hours\"" +
            "\nFROM \"hs_base_package\"" +
            "\nGROUP BY time(1h), person_zid, device_id");
    log.info("result ===>: {}", JSON.toJSONString(result));
  }

  @Test
  void testQuery() {
    QueryResult result = template.postQuery("SHOW MEASUREMENTS ON test;");
    log.info("result ===>: {}", JSON.toJSONString(result));
  }

  @Test
  void testQuery2() {
    Map<String, FieldKey> fieldKeyMap = template.getFieldKeys("hs_wave_package", true);
    log.info("fieldKeyMap ==>: \n{}\n", JSON.toJSONString(fieldKeyMap));
    template.query("SELECT *  FROM hs_wave_package WHERE patient_id = '0ad66d27dd4f4bd3a8d836dc0977b85d' order by time desc limit 10")
        .subscribe(new SimpleSubscriber<QueryResult>() {
          @Override
          public void onNext(QueryResult result) {
            log.info("result ===>: \n{}\n", JSON.toJSONString(result));
          }
        });
  }

  @Test
  void testQuery3() {
    File dir = new File("./build/influxdb/");
    log.info("dir -->: {}", dir.getAbsolutePath());
    BiConsumer<String, Object> writeFile = (filename, value) -> {
      IOUtils.write(JSON.toJSONString(value, JSONWriter.Feature.PrettyFormat).getBytes(StandardCharsets.UTF_8), IOUtils.createFile(new File(dir, filename + ".json")));
      log.info("{} ===>: \n{}\n", filename, JSON.toJSONString(value));
    };
    writeFile.accept("fieldKeys1", template.getFieldKeys("hs_wave_package", false));
    writeFile.accept("tagKeys", template.getTagKeys("hs_wave_package"));
    template.query("SELECT *  FROM hs_wave_package WHERE time >= '2024-08-01T00:00:00Z' AND time < now() LIMIT 100")
        .subscribe(SimpleSubscriber.create(result -> {
          writeFile.accept("queryResult", result);
          Map<String, FieldKey> fieldKeys = template.getFieldKeys("hs_wave_package", true);
          writeFile.accept("fieldKeys2", fieldKeys);
          List<Point> points = InfluxUtils.toPoint(result, fieldKeys, false);
          writeFile.accept("points", points);
        }));
  }

  @Test
  void testQuery4() {
    QueryResult queryResult = JSON.parseObject(IOUtils.readAsString(new File("./build/influxdb/QueryResult.json")), QueryResult.class);
    queryResult
        .getResults()
        .stream()
        .map(r -> StringUtils.isBlank(r.getError()) ? r.getSeries() : null)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .forEach(s -> {
          List<JSONObject> jsonList = s.getValues()
              .stream()
              .map(values -> {
                List<String> columns = s.getColumns();
                JSONObject json = new JSONObject();
                for (int i = 0; i < values.size(); i++) {
                  String key = columns.get(i);
                  Object value = values.get(i);
                  json.put(key, value);
                }
                return json;
              })
              .collect(Collectors.toList());
          log.info("jsonList ===>: \n{}\n", JSON.toJSONString(jsonList));
        });
  }

  @Test
  void testWrite() {
    String line = generateLine();
    log.info(line);
//    template.write(line);

    writeManager.write(line);
    writeManager.flush();
  }

  @Test
  void testWriteFile() {
    template.write(new File("D:/.tmp/cache/influxdb/hs_alarm.line"));
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
    tr.setDescription("描述: " + DateFmtter.fmtNow("yyyy年MM月dd日 HH时mm分ss秒"));
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
    template.query("SELECT * FROM hs_wave_package WHERE time >= 1d GROUP BY person_zid LIMIT 10;", 100)
        .subscribe(new QuerySubscriber() {
          @Override
          public void onNext0(@NotNull QueryResult result) {
            System.err.println(JSON.toJSONString(result));
          }
        });
  }

  @Test
  void testQueryChunk() {
    IWriter writer = IWriter.create("D:/.tmp/cache/influxdb/" + IdUtils.uuid() + ".line", false);
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
            writer.flush();
            writer.close();
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
    Map<String, FieldKey> fieldKeyMap = template.getFieldKeys("hs_wave_package", true);
    log.info("fieldKeyMap: \n{}", JSON.toJSONString(fieldKeyMap, JSONWriter.Feature.PrettyFormat));
  }

  @Test
  void test_createContinuousQuery() {
    long startTime = TimeUtils.getYesterday(0, 0, 0);
    long endTIme = TimeUtils.getToday(0, 0, 0);
    String subSql = String.format("SELECT count(package_sn) AS count" +
            " FROM hs_wave_package" +
            " WHERE time >= '%s' AND time <= '%s' AND device_id != person_zid" +
            " GROUP BY person_zid, device_id"
        , DateFmtter.fmtUtc(startTime)
        , DateFmtter.fmtUtc(endTIme)
    );
    String sql = String.format("SELECT *" +
        " INTO hs_data_statistic" +
        " FROM (%s)" +
        " WHERE count > 0", subSql);
    log.info("test_createContinuousQuery: {}", sql);
//    QueryResult result = template.createContinuousQuery("data_statistic_1d", template.getDatabase(), sql);
//    log.info("result: {}", JSON.toJSONString(result));
  }

  /**
   * 导入 line 文件
   */
  @Test
  void test_delete() {
    long startTime = TimeUtils.toDate(2000, 2, 10, 0, 0, 0).getTime();
    long endTime = TimeUtils.toDate(2024, 2, 7, 23, 59, 0).getTime();
    String condition = "";
    for (String measurement : template.getMeasurements()) {
      String sql = "delete from " + measurement + " where"
          + " time >= '" + DateFmtter.fmtUtc(startTime) + "'"
          + " AND time <= '" + DateFmtter.fmtUtc(endTime) + "'"
          + StringUtils.getIfBlank(condition, () -> "");
      List<QueryResult.Result> results = template.postQuery(template.getDatabase(), sql).getResults();
      System.err.println(sql + "  ==>: \n " + JSON.toJSONString(results));
    }
  }


  /**
   * 导出 line 文件
   */
  @Test
  void test_exportLines() {
    long startTime = TimeUtils.toDate(2024, 5, 31, 20, 0, 0).getTime();
    long endTime = TimeUtils.toDate(2024, 6, 1, 23, 59, 59).getTime();
//    long endTime = TimeUtils.now();
    String condition = " AND patient_id = '0343b92e17444b2386756649e55c816c'";
//    String condition = " AND patient_id = 'ef372fd4b5254bf58828cc297a0efc61'";
//    String condition = " AND (person_zid = 'f89ebf339a33442fbd0cf5764c7868f5' OR patient_id = 'f89ebf339a33442fbd0cf5764c7868f5')";
//    String condition = " AND device_no = '641938001136'";
//    String condition = " AND (device_id != person_zid AND device_id != '01001080' AND device_id != '01001169' AND device_id != '01001148' AND device_id != '01001149' AND device_id != '01001192') ";
//    String condition = " AND (" +
////        Stream.of("374e0249d96541e292a381ff433e6279", "a8a3954569ca4034a472bae6c70f7fe4", "374e0249d96541e292a381ff433e6279", "d8d4f6ec9936416fb40e4e56853a8eb5")
//        Stream.of("d8d4f6ec9936416fb40e4e56853a8eb5")
//            .distinct()
//            .map(id -> "person_zid = '" + id + "'")
//            .collect(Collectors.joining(" OR "))
//        + ")";
//    String condition = "";
    File dir = IOUtils.createFile("D:/.tmp/cache/influxdb", true);
    exportAll(template, dir, startTime, endTime, condition, name -> !name.endsWith("_point"));

  }

  void exportAll(InfluxTemplate template, File dir, long startTime, long endTime, String condition, Predicate<String> measurementFilter) {
    template.getMeasurements()
        .stream()
        .filter(measurementFilter) // 不保存波形趋势
        .map(name -> MeasurementInfo.builder()
            .name(name)
            .fieldKeys(template.getFieldKeys(name, true))
            .build())
        .forEach(measurementInfo -> {
          File line = IOUtils.createFile(dir, measurementInfo.name + ".line");
          template.export(line, measurementInfo.name, 1000, startTime, endTime, condition);
          if (line.length() <= 20) {
            line.delete(); // 没有数据，删除空文件
          }
        });
  }

  @SuperBuilder
  @Data
  public static class MeasurementInfo {
    String name;
    Map<String, FieldKey> fieldKeys;
  }

  /**
   * 导入 line 文件
   */
  @Test
  void test_loadLines() {
//    test_createSubscriptions();
    File dir = new File("D:/.tmp/cache/influxdb");
    File[] lines = dir.listFiles(f -> f.length() > 20
        //&& f.getName().endsWith(".line")
        //&& f.getName().endsWith(".point")
        && (f.getName().endsWith(".line") || f.getName().endsWith(".point"))
    );
    assert lines != null;
    //upload(template, Arrays.asList(lines), true);
    upload(template, Arrays.asList(lines), false);
  }

  /**
   * 测试解析行协议
   */
  @Test
  void test_parseLine() {
    String line = "hs_wave_package,device_id=01000927,patient_id=fde43e9dc45945d4bac40e3e0053664f,person_id=01000927,person_zid=fde43e9dc45945d4bac40e3e0053664f abdomina_conn_state=0i,abdominal_resp_points=\"[700,708,716,724,732,736,744,748,752,756,760,760,760,764,764,764,764,764,760,760,760,756,752,748,744]\",ca_ratio=0i,ecg_conn_state=0i,ecg_points=\"[502,502,502,502,502,502,502,502,502,502,502,500,498,496,492,500,518,554,592,618,620,600,562,526,502,492,492,496,498,498,498,498,498,498,498,498,498,498,500,500,500,500,500,500,500,500,500,500,500,500,500,500,502,502,504,508,512,516,520,524,526,530,534,538,540,542,542,542,542,542,542,542,542,540,536,532,528,524,518,512,508,504,500,498,496,496,496,496,496,496,496,496,496,496,498,498,498,498,498,498,498,498,498,498,498,498,498,500,500,498,498,498,500,500,500,500,500,500,500,500,502,502,502,502,502,504,508,512,516,520,524,524,526,524,522,518,514,510,506,502,502,502,500,500,502,502,502,502,502,502,502,502,502,502,502,500,498,496,492,500,518,554,592,618,620,600,562,526,502,492,492,496,498,498,498,498,498,498,498,498,498,498,500,500,500,500,500,500,500,500,500,500,500,500,500,500,502,502,504,508]\",ei_ratio=91i,package_sn=637719i,resp_conn_state=0i,resp_points=\"[592,600,612,624,636,648,660,672,684,692,700,708,716,724,732,736,744,748,752,756,760,760,760,764,764]\",simulate=false,spo2_points=\"[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]\",x_points=\"[525,524,529,526,525,524,524,522,526,527,525,525,523,524,527,528,525,526,528,525,524,525,524,524,527]\",y_points=\"[512,511,512,511,513,510,512,511,514,511,510,512,512,511,511,513,512,512,512,512,510,511,512,512,512]\",z_points=\"[639,638,638,638,639,635,636,637,638,638,637,638,638,636,637,638,639,640,638,637,638,639,637,637,637]\" 1692069600000000000";
    LineProtocol lineProtocol = InfluxUtils.parseLine(line);
    log.info("lineProtocol ==>: \n{}\n", JSON.toJSONString(lineProtocol));
    InfluxWavePackage pkg = lineProtocol.toPoJo(InfluxWavePackage.class);
    log.info("pkg: \n{}", JSON.toJSONString(pkg, JSONWriter.Feature.PrettyFormat));
  }

  /**
   * 测试解析行协议
   */
  @Test
  void test_parseLine2() {
    File lineFile = new File("D:/.tmp/cache/influxdb/hs_darma_mattress.line");
//    String type = "hr";
    String type = "rr";
    List<Integer> points = IOUtils.readLines(IOUtils.newBufferedReader(lineFile, StandardCharsets.UTF_8))
        .stream()
        .map(InfluxUtils::parseLine)
        .map(line -> JSON.parseObject((String) line.getFields().get(type + "_points"), Integer[].class))
        .filter(Objects::nonNull)
        .flatMap(Stream::of)
        .collect(Collectors.toList());
    IWriter writer = IWriter.create(IOUtils.createFile(lineFile.getParentFile(), lineFile.getName().replace(".line", "_" + type + ".csv")), false);
    String jsonString = JSON.toJSONString(points);
    writer.write(jsonString.substring(1, jsonString.length() - 2)).flush();
    writer.close();
  }

  /**
   * 转换数组为点：采集器
   */
  @Test
  void test_WaveToPoints() {
    File dir = IOUtils.createFile("D:/.tmp/cache/influxdb", true);

    InfluxOptions srcOptions = BeanHelper.copy(options);
//    srcOptions.setUrl("http://39.98.251.12:58086");
//    srcOptions.setUrl("http://research.sensecho.com:58086");
//    srcOptions.setUrl("http://192.168.1.211:58039");
    srcOptions.setUrl("http://192.168.1.198:58086");
    srcOptions.setDatabase("hsrg");
    srcOptions.setUsername("admin");
    srcOptions.setPassword("hsrg8888");
    InfluxTemplateImpl srcTemplate = new InfluxTemplateImpl();
    srcTemplate.setConverterFactory(converterFactory);
    srcTemplate.setOptions(srcOptions);
    srcTemplate.setApi(factory.create(srcOptions));
    srcTemplate.setJsonAdapter(new Moshi.Builder().build().adapter(QueryResult.class));

    long startTime = TimeUtils.toDate(2023, 10, 24, 0, 0, 0).getTime();
    long endTime = TimeUtils.toDate(2023, 10, 25, 0, 0, 0).getTime();
    log.info("startTime: {}, endTime: {}", DateFmtter.fmt(startTime), DateFmtter.fmt(endTime));
//    String condition = " AND device_id = '11000138'";
    String condition = " AND person_zid = 'bb00f55818c54e4380d8f461224413f1'";
    //String condition = "";
//    boolean exportPoint = false;
    boolean exportPoint = true;
    if (exportPoint) {
      waveToPoints(srcTemplate
          , dir
          , "hs_wave_package"
          , startTime
          , endTime
          , condition
          , (line, base) -> mapWaveToPoints(base, "hs_wave_point"
              , "ecg_points", "resp_points", "abdominal_resp_points"//, "spo2_points", "x_points", "y_points", "z_points"
          )
      );

      waveToPoints(srcTemplate
          , dir
          , "hs_teleecg_wave_package"
          , startTime
          , endTime
          , condition
          , (line, base) -> mapWaveToPoints(base, "hs_teleecg_wave_point"
              , "I", "II", "III", "V1", "V2", "V3", "V4", "V5", "V6", "aVR", "aVL", "aVF")
      );
    }

//    // 导出全部的数据
//    exportAll(srcTemplate, dir, startTime, endTime, condition, name -> !name.endsWith("_point"));

    List<File> lines = Stream.of(Objects.requireNonNull(dir.listFiles()))
        .filter(f -> f.length() > 20)
//        .filter(f -> f.getName().endsWith(".point") || f.getName().endsWith(".line"))
        .filter(f -> f.getName().endsWith(".point"))
        .collect(Collectors.toList());
    upload(template, lines, true);

  }

  @Test
  void test_statistic() {
    String sql = "SELECT ecg, chResp, abdResp, spo2" +
        " FROM " +
        " (SELECT COUNT(ecg_points) AS ecg, COUNT(resp_points) AS chResp, COUNT(abdominal_points) AS abdResp FROM hs_wave_package WHERE time >= '2023-08-23T16:00:00Z' AND time < '2023-08-24T16:00:00Z' AND device_id != patient_id AND patient_id != '' GROUP BY patient_id, device_id)" +
        " ,(SELECT COUNT(package_sn) AS spo2 FROM hs_base_package WHERE time >= '2023-08-23T16:00:00Z' AND time < '2023-08-24T16:00:00Z' AND device_id != patient_id AND patient_id != '' AND spo2_conn_state = 0 GROUP BY patient_id, device_id)";
    log.error("sql: \n{}\n", sql);
    Flowable.just(template.postQuery(template.getDatabase(), sql))
        .subscribe(result -> {
          log.info("result: \n{}", JSON.toJSONString(result));
        }, Throwable::printStackTrace);

    template.query("SELECT COUNT(ecg_points) AS ecg, COUNT(resp_points) AS chResp, COUNT(abdominal_points) AS abdResp FROM hs_wave_package WHERE time >= '2023-08-23T16:00:00Z' AND time < '2023-08-24T16:00:00Z' AND device_id != patient_id AND patient_id != '' GROUP BY patient_id, device_id")
        .subscribe(result -> {
          log.info("result2: \n{}", JSON.toJSONString(result));
        }, Throwable::printStackTrace);
  }

  void upload(InfluxTemplate template, List<File> lines, boolean delete) {
    for (File line : lines) {
      log.info("upload file: {}", line);
      AtomicLong prev = new AtomicLong(0);
      AtomicLong current = new AtomicLong(0);
      template.write(new ProgressRequestBody(RequestBody.create(line, InfluxTemplate.MEDIA_TYPE_STRING), (totalLength, progress, done) -> {
        log.info("file[{}], totalLength: {}, progress: {}, percent: {}, done: {}", line, totalLength, progress, progress * 100f / totalLength, done);
        prev.set(current.get());
        current.set(progress);
      }));
      if (delete) line.delete();
    }
  }

  static List<String> mapWaveToPoints(LineProtocol base, String measurement, String... columns) {
    Map<String, Object> baseFields = base.getFields();
    LineProtocol copy = base.copy();
    copy.setMeasurement(measurement);
    copy.getFields().clear();
    // 通用数字段
    baseFields.forEach((k, v) -> {
      if (!(v instanceof String
          && ((String) v).startsWith("[")
          && ((String) v).endsWith("]"))) {
        copy.getFields().put(k, v);
      }
    });

    List<Integer> respList = new ArrayList<>(25);
    List<String> lines = new LinkedList<>();
    List<WaveColumn> waveList = Stream.of(columns)
        .map(column -> new WaveColumn(column, column.replace("_points", ""), JSON.parseObject((String) baseFields.get(column), int[].class), 1))
        .collect(Collectors.toList());
    WaveColumn maxColumn = waveList.stream()
        .max(Comparator.comparingInt(o -> o.wave.length))
        .orElse(null);
    int maxLen = maxColumn.wave.length;
    waveList.forEach(wv -> wv.setRatio(maxLen / wv.wave.length));
    long interval = 1000L / maxLen;
    for (int i = 0; i < maxLen; i++) {
      copy.setTime(base.getTime() + TimeUnit.MILLISECONDS.toNanos(i * interval));
      Point.Builder point = copy.toPointBuilder();
      for (WaveColumn wc : waveList) {
        if (i % wc.ratio == 0) {
          point.addField(wc.name, wc.wave[i / wc.ratio]);
        } else {
          point.removeField(wc.name + "_conn_state");
        }
      }
      if (!point.hasField("abdominal_resp")) {
        point.removeField("abdomina_conn_state");
      }
      lines.add(point.build().lineProtocol());
    }
    return lines;
  }


  static void waveToPoints(InfluxTemplate template,
                           File dir,
                           String measurement,
                           long startTime,
                           long endTime,
                           String condition,
                           BiFunction<String, LineProtocol, List<String>> converter) {
    File file = new File(dir, measurement + ".line");
    // 导出line文件
    template.export(file, measurement, 1000, startTime, endTime, condition);
    if (file.length() <= 0) {
      file.delete();
      return;
    }
    AtomicReference<FileWriterImpl> writerRef = new AtomicReference<>();
    AtomicInteger index = new AtomicInteger(1);
    IOUtils.readLines(file, (line, num) -> {
      if (StringUtils.isBlank(line)) {
        return;
      }
      List<String> lines = converter.apply(line, InfluxUtils.parseLine(line));
      if (writerRef.get() == null) {
        String filename = file.getName().replace(".line", "_" + index.getAndIncrement() + ".point");
        writerRef.set(new FileWriterImpl(IOUtils.createFile(dir, filename), false));
      }
      FileWriterImpl writer = writerRef.get();
      writer.writeAndFlush(String.join("\n", lines), "\n");
      if (writer.length() > 100 * Utils.MB) {
        writerRef.getAndSet(null).close();
      }
    });
    FileWriterImpl writer = writerRef.get();
    if (writer != null) {
      writer.flush();
      writer.close();
    }
  }

  @SuperBuilder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class WaveColumn {
    String rawName;
    String name;
    int[] wave;
    int ratio;
  }

  static final String ARR_200;
  static final String ARR_25;
  static final String ARR_50;

  static {
    int[] arr200 = new int[200];
    Arrays.fill(arr200, 512);
    ARR_200 = JSON.toJSONString(arr200);

    int[] arr25 = new int[25];
    Arrays.fill(arr25, 0);
    ARR_25 = JSON.toJSONString(arr25);

    int[] arr50 = new int[50];
    Arrays.fill(arr50, 0);
    ARR_50 = JSON.toJSONString(arr200);
  }

  @Test
  void test_exportSleep() {
    String personZid = "0f7c59ae2a6f4b6b99f0adc9963ef2e3";
    String startTime = DateFmtter.fmtUtc(TimeUtils.toDate(2023, 9, 13, 21, 0, 0));
    String endTime = DateFmtter.fmtUtc(TimeUtils.toDate(2023, 9, 14, 8, 0, 0));

    String clause = String.format(" WHERE person_zid = '%s' AND time >= '%s' AND time < '%s'", personZid, startTime, endTime);
    Map<Long, JSONObject> allRates = new LinkedHashMap<>();
    template.query("SELECT first(heart_rate) AS hr, first(resp_rate) AS rr, first(spo2) AS spo2, first(gesture) AS gesture" +
            " FROM hs_all_rates" + clause +
            " GROUP BY time(1s) fill(null)")
        .subscribe(new QueryObserver() {
          @Override
          public void onSeriesNext(List<Object> values, ValueConverter c, int position) {
            allRates.put(c.getTime() / 1000L, new JSONObject() {{
              put("hr", c.getInteger("hr"));
              put("rr", c.getInteger("rr"));
              put("spo2", c.getInteger("spo2"));
              put("gesture", c.getInteger("gesture"));
            }});
          }
        });
    String sql = "SELECT" +
        " first(ecg_points) AS ecg" +
        ", first(resp_points) AS chResp" +
        ", first(abdominal_resp_points) AS abdResp" +
        ", first(x_points) AS x" +
        ", first(y_points) AS y" +
        ", first(z_points) AS z" +
        " FROM hs_wave_package" + clause + " GROUP BY time(1s) fill(null)";
    log.info("sql: \n{}\n", sql);
    IWriter writer = IWriter.create(IOUtils.createFile("D:/.tmp/cache/sleepData.dat"), false);
    template.query(sql)
        .subscribe(new QueryObserver() {
          @Override
          public void onSeriesNext(List<Object> values, ValueConverter c, int position) {
            JSONObject rates = allRates.get(c.getTime() / 1000L);
            SleepPacket pkg = SleepPacket.builder()
                .time(c.getTime() / 1000L)
                .ecg(JSON.parseObject(c.getString("ecg", ARR_200), int[].class))
                .chResp(JSON.parseObject(c.getString("chResp", ARR_25), int[].class))
                .abdResp(JSON.parseObject(c.getString("abdResp", ARR_25), int[].class))
                .x(JSON.parseObject(c.getString("x", ARR_25), int[].class))
                .y(JSON.parseObject(c.getString("y", ARR_25), int[].class))
                .z(JSON.parseObject(c.getString("z", ARR_25), int[].class))
                .hr(rates != null ? rates.getIntValue("hr", 0) : 0)
                .rr(rates != null ? rates.getIntValue("rr", 0) : 0)
                .spo2(rates != null ? rates.getIntValue("spo2", 0) : 0)
                .gesture(rates != null ? rates.getIntValue("gesture", 0) : 0)
                .build();
            byte[] bytes = JavaStructManager.get().toBytes(pkg);
//            log.info("time: {}, data[{}]: {}, {}"
//                , DateFmtter.fmt(c.getTime())
//                , bytes.length
//                , HexUtils.bytesToHex(bytes)
//                , JSON.toJSONString(pkg)
//            );
            writer.writeAndFlush(bytes);
          }
        });
    writer.flush();
    writer.close();
  }

  @Test
  void test_Spo2() {
    IWriter writer = IWriter.create(new File("D:/.tmp/cache/influxdb/tmp.txt"), false);
    template.query("select * from hs_oximeter_package where time > now() - 1d")
        .subscribe(new QueryObserver() {
          JSONObject json = new JSONObject(new LinkedHashMap());

          @Override
          public void onSeriesNext(List<Object> values, ValueConverter c, int position) {
            json.put("time", c.getTime());
            json.put("date", DateFmtter.fmt(c.getTime()));
            json.put("points", JSON.parseObject(c.getString("points"), int[].class));
            writer.write(json.toJSONString());
            writer.writeAndFlush("\n");
          }
        });
  }

  @Test
  void test_write() {
    String base64 = IOUtils.readAsString(new File("D:/.tmp/cache/influxdb/base64.txt"));
    byte[] decode = Base64.getDecoder().decode(base64);
    String str = new String(decode, StandardCharsets.UTF_8);
    JSONObject json = JSON.parseObject(str);
    byte[] pdf = Base64.getDecoder().decode(json.getJSONObject("postDataReport").getString("PDFContent"));
    IOUtils.write(pdf, new File("D:/.tmp/cache/influxdb/test.pdf"), true);
    log.info("str ==>: {}", str);
  }


//  @Test
//  void test_parse() {
//    List<JSONObject> list = IOUtils.readLines(new File("D:/.tmp/cache/data.txt"))
//        .stream()
//        .map(line -> line.split("\t"))
//        .map(arr -> new JSONObject(){{
//          put("patientId", arr[0]);
//          put("personName", arr[1]);
//          put("itemId", arr[2]);
//          put("createTime", arr[3]);
//          put("recipelId", arr[4]);
//          put("status", arr[5]);
//          put("startTime", arr[6]);
//          put("endTime", arr.length >= 8 ? arr[7] : null);
//        }})
//        .collect(Collectors.toList());
//    log.info(" ==>: \n{}", JSON.toJSONString(list));
//
//    for (JSONObject json : list) {
//      File dir = IOUtils.mkDirs("D:/.tmp/cache/data/" + json.getString("personName"));
//      log.info("dir: {}, json: {}", dir, json.toJSONString());
//      long startTime = DateFmtter.parseToLong(json.getString("startTime"), "yyyy-MM-dd HH:mm:ss.SSS");
//      long endTime = "finish".equalsIgnoreCase(json.getString("status"))
//          ? DateFmtter.parseToLong(json.getString("endTime"), "yyyy-MM-dd HH:mm:ss.SSS")
//          : DateFmtter.now();
//      String condition = " AND person_zid = '" + json.getString("patientId") + "'";
//
//      File wave = IOUtils.createFile(dir, "生理信号.line");
//      try {
//        IWriter writer = IWriter.createWriter(wave, false);
//        template.export((queryResult, fieldKeyMap) -> {
//          List<JSONObject> points = InfluxUtils.toPoint(queryResult, fieldKeyMap)
//              .stream()
//              .map(p -> {
//                JSONObject pjson = new JSONObject();
//                pjson.put("time", p.getTime().longValue());
//                pjson.put("patient_id", p.getTags().get("person_zid"));
//                pjson.put("device_id", p.getTags().get("device_id"));
//                pjson.putAll(p.getFields());
//                return pjson;
//              })
//              .collect(Collectors.toList());
//          points.forEach(p -> writer.writeAndFlush(p.toJSONString()).writeAndFlush("\n"));
//        }, "hs_wave_package", 100, startTime, endTime, condition);
//        IOUtils.closeQuietly(writer);
//      } finally {
//        if (wave.length() <= 20) wave.delete(); // 没有数据，删除空文件
//      }
//
//      File trend = IOUtils.createFile(dir, "趋势数据.line");
//      try {
//        IWriter writer = IWriter.createWriter(trend, false);
//        template.export((queryResult, fieldKeyMap) -> {
//          List<JSONObject> points = InfluxUtils.toPoint(queryResult, fieldKeyMap)
//              .stream()
//              .map(p -> {
//                JSONObject pjson = new JSONObject();
//                pjson.put("time", p.getTime().longValue());
//                pjson.put("patient_id", p.getTags().get("person_zid"));
//                pjson.put("device_id", p.getTags().get("device_id"));
//                pjson.putAll(p.getFields());
//                return pjson;
//              })
//              .collect(Collectors.toList());
//          points.forEach(p -> writer.writeAndFlush(p.toJSONString()).writeAndFlush("\n"));
//        }, "hs_all_rates", 1000, startTime, endTime, condition);
//      } finally {
//        if (trend.length() <= 20) trend.delete(); // 没有数据，删除空文件
//      }
//    }
//  }

  @Test
  void test_count() {
    long startTime = TimeUtils.toTime(2024, 4, 25);
    long endTime = TimeUtils.toTime(2024, 4, 26);
    String condition = "AND (ecg_conn_state = '0' OR ecg_lead_state = 0 OR resp_conn_state = 0)";
    CountInfo info = template.queryCountInfo("hs_wave_package", "ecg_points", startTime, endTime, " AND patient_id = '23dfdebf4302482fa4f75c38046eb4d9' " + condition);
    log.info("info ==>:\n\n{}\n", JSON.toJSONString(info, JSONWriter.Feature.PrettyFormat));
  }
}
