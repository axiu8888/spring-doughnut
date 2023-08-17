package com.benefitj.spring.influxdb;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.benefitj.core.*;
import com.benefitj.core.file.FileWriterImpl;
import com.benefitj.core.file.IWriter;
import com.benefitj.http.ProgressRequestBody;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.influxdb.convert.PointConverterFactory;
import com.benefitj.spring.influxdb.dto.FieldKey;
import com.benefitj.spring.influxdb.dto.LineProtocol;
import com.benefitj.spring.influxdb.dto.Point;
import com.benefitj.spring.influxdb.dto.QueryResult;
import com.benefitj.spring.influxdb.pojo.InfluxWavePackage;
import com.benefitj.spring.influxdb.spring.InfluxConfiguration;
import com.benefitj.spring.influxdb.template.*;
import com.squareup.moshi.Moshi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(classes = InfluxConfiguration.class)
@Slf4j
public class InfluxApiDBApiTest {

  @Autowired
  InfluxTemplate template;

  @Autowired
  InfluxOptions options;

  @Autowired
  InfluxApiFactory factory;

  @Autowired
  PointConverterFactory converterFactory;

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
    Map<String, FieldKey> fieldKeyMap = template.getFieldKeyMap(template.getDatabase(), template.getRetentionPolicy(), "hs_wave_package", true);
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

    Long startTime = DateFmtter.parseToLong("2023-08-15 11:20:00");
    Long endTime = DateFmtter.parseToLong("2023-08-15 14:17:59");
    String condition = " AND person_zid = 'fde43e9dc45945d4bac40e3e0053664f'";
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

  @SuperBuilder
  @Data
  public static class MeasurementInfo {
    String name;
    Map<String, FieldKey> fieldKeyMap;
  }

  /**
   * 导入 line 文件
   */
  @Test
  void test_loadLines() {
    File dir = new File("D:/tmp/influxdb");
//    File[] lines = dir.listFiles(pathname -> pathname.getName().endsWith(".line") && pathname.length() > 0);
    File[] lines = dir.listFiles(pathname -> pathname.getName().endsWith(".point") && pathname.length() > 0);
    assert lines != null;
    for (File line : lines) {
      log.info("upload file: {}", line);
      AtomicLong prev = new AtomicLong(0);
      AtomicLong current = new AtomicLong(0);
      template.write(new ProgressRequestBody(RequestBody.create(line, InfluxTemplate.MEDIA_TYPE_STRING), (totalLength, progress, done) -> {
        log.info("file[{}], totalLength: {}, progress: {}, done: {}", line, totalLength, progress, done);
        prev.set(current.get());
        current.set(progress);
      }));
      //line.delete();
    }
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
    log.info("pkg: \n{}", JSONUtil.toJsonPrettyStr(pkg));
  }

  /**
   * 转换数组为点：采集器
   */
  @Test
  void test_WaveToPoints() {
    File dir = IOUtils.createFile("D:/tmp/influxdb", true);

    InfluxOptions srcOptions = BeanHelper.copy(options);
    //srcOptions.setUrl("http://39.98.251.12:58086");
    srcOptions.setUrl("http://192.168.1.198:58086");
    srcOptions.setDatabase("hsrg");
    srcOptions.setUsername("admin");
    srcOptions.setPassword("hsrg8888");
    InfluxTemplateImpl srcTemplate = new InfluxTemplateImpl();
    srcTemplate.setConverterFactory(converterFactory);
    srcTemplate.setOptions(srcOptions);
    srcTemplate.setApi(factory.create(srcOptions));
    srcTemplate.setJsonAdapter(new Moshi.Builder().build().adapter(QueryResult.class));

    long startTime = TimeUtils.getToday(9, 0, 0);
    long endTime = TimeUtils.getToday(12, 0, 0);
    waveToPoints(srcTemplate
        , dir
        , "hs_wave_package"
        , startTime
        , endTime
        , ""
        , (line, base) -> mapWaveToPoints(base, "hs_wave_point"
            , "ecg_points", "spo2_points", "resp_points", "abdominal_resp_points", "x_points", "y_points", "z_points")
    );

//    waveToPoints(srcTemplate
//        , dir
//        , "hs_teleecg_wave_package"
//        , startTime
//        , endTime
//        , "AND person_zid = '33f57291ff414ce497175e616b5b830b'"
//        , (line, base) -> mapWaveToPoints(base, "hs_teleecg_wave_point"
//            , "I", "II", "III", "V1", "V2", "V3", "V4", "V5", "V6", "aVR", "aVL", "aVF")
//    );

    File[] lines = dir.listFiles(pathname -> pathname.getName().endsWith(".point") && pathname.length() > 0);
    assert lines != null;
    for (File line : lines) {
      log.info("upload file: {}", line);
      AtomicLong prev = new AtomicLong(0);
      AtomicLong current = new AtomicLong(0);
      template.write(new ProgressRequestBody(RequestBody.create(line, InfluxTemplate.MEDIA_TYPE_STRING), (totalLength, progress, done) -> {
        log.info("file[{}], totalLength: {}, progress: {}, done: {}", line, totalLength, progress, done);
        prev.set(current.get());
        current.set(progress);
      }));
      //line.delete();
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

    List<String> lines = new LinkedList<>();
    List<WaveColumn> waveList = Stream.of(columns)
        .map(column -> new WaveColumn(column, column.replace("_points", ""), JSON.parseObject((String) baseFields.get(column), int[].class)))
        .collect(Collectors.toList());
    WaveColumn maxColumn = waveList.stream()
        .min((o1, o2) -> Integer.compare(o2.wave.length, o1.wave.length))
        .orElse(null);
    long maxLen = maxColumn.wave.length;
    long interval = 1000L / maxLen;
    for (int i = 0; i < maxLen; i++) {
      copy.setTime(base.getTime() + TimeUnit.MILLISECONDS.toNanos(i * interval));
      Point.Builder point = copy.toPointBuilder();
      for (WaveColumn wc : waveList) {
        if (i % (maxLen / wc.wave.length) == 0) {
          point.addField(wc.name, wc.wave[i % wc.wave.length]);
        }
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
    File lineFile = new File(dir, measurement + ".line");
    // 导出line文件
    template.export(lineFile, measurement, 1000, startTime, endTime, condition);
    AtomicReference<FileWriterImpl> writerRef = new AtomicReference<>();
    AtomicInteger index = new AtomicInteger(1);
    IOUtils.readLines(lineFile, line -> {
      List<String> lines = converter.apply(line, InfluxUtils.parseLine(line));
      if (writerRef.get() == null) {
        String filename = lineFile.getName().replace(".line", "_" + index.getAndIncrement() + ".point");
        writerRef.set(new FileWriterImpl(IOUtils.createFile(dir, filename)));
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
  }
}
