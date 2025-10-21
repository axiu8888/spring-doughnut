package com.benefitj.reportexport;

import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.file.FileWriterImpl;
import com.benefitj.core.file.IWriter;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.influxdb.dto.CountInfo;
import com.benefitj.spring.influxdb.dto.LineProtocol;
import com.benefitj.spring.influxdb.spring.EnableInfluxdb;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import com.benefitj.spring.influxdb.template.QueryObserver;
import com.benefitj.spring.influxdb.template.ValueConverter;
import com.benefitj.spring.listener.AppStateHook;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@EnableInfluxdb
@SpringBootApplication
@Slf4j
public class ReportExportApp {
  public static void main(String[] args) {
    SpringApplication.run(ReportExportApp.class, args);
  }

  static {
    AppStateHook.registerStart(evt -> onAppStart());
  }

  public static void onAppStart() {
    InfluxTemplate influxTemplate = SpringCtxHolder.getBean(InfluxTemplate.class);
    MongoTemplate mongoTemplate = SpringCtxHolder.getBean(MongoTemplate.class);

    Options opts = SpringCtxHolder.getBean(Options.class);

    String[] tables = opts.tables.split(",");
    String reportType = opts.reportType;
    String reportId = opts.reportId;
    String personZid = opts.personZid;
    long startTime = DateFmtter.parseToLong(opts.startTime);
    long endTime = DateFmtter.parseToLong(opts.endTime);
    String dir = (opts.destDir + "/").replace("\\", "/").replace("//", "/");

    if (StringUtils.isAnyBlank(reportId)) {
      System.err.println("错误 ==>: 请配置报告ID！");
      EventLoop.asyncIO(() -> System.exit(0), 5000);
      return;
    }

    JSONObject report = StringUtils.isNotBlank(reportType)
        ? mongoTemplate.findById(reportId, JSONObject.class, reportType)
        : null;
    if (report == null) {
      Set<String> collectionNames = mongoTemplate.getCollectionNames();
      for (String collectionName : collectionNames) {
        report = mongoTemplate.findById(reportId, JSONObject.class, collectionName);
        if (report != null) {
          reportType = collectionName;
          break;
        }
      }
      if (report == null) {
        System.err.println("错误 ==>: 无法查找到对应报告, " + reportId);
        EventLoop.asyncIO(() -> System.exit(0), 5000);
        return;
      }
    }
    if (opts.tables.trim().equals("*")) {
      tables = influxTemplate.getMeasurements().toArray(new String[0]);
    }

    String personName = report.getString("personName");
    personName = StringUtils.isBlank(personName) ? report.getString("person_name") : personName;
    personName = StringUtils.isBlank(personName) ? "" : personName;
    dir = dir + personName + "__" + reportType + "__"+ reportId + "/";
    String task_startTime = report.getString("task_startTime");
    task_startTime = StringUtils.isBlank(task_startTime) ? report.getString("startTime") : task_startTime;
    task_startTime = StringUtils.isBlank(task_startTime) ? report.getString("start_time") : task_startTime;
    String task_endTime = report.getString("task_endTime");
    task_endTime = StringUtils.isBlank(task_endTime) ? report.getString("endTime") : task_endTime;
    task_endTime = StringUtils.isBlank(task_endTime) ? report.getString("end_time") : task_endTime;
    startTime = StringUtils.isNotBlank(task_startTime) ? DateFmtter.parseToLong(task_startTime) : startTime;
    endTime = StringUtils.isNotBlank(task_endTime) ? DateFmtter.parseToLong(task_endTime) : endTime;
    try (final IWriter w = IWriter.create(IOUtils.createFile(dir + "report.json"), false)) {
      w.writeAndFlush(report.toString());
    }

    String _personZid = personZid;
    personZid = StringUtils.isBlank(personZid) ? report.getString("personZid") : personZid;
    personZid = StringUtils.isBlank(personZid) ? report.getString("person_zid") : personZid;
    personZid = StringUtils.isBlank(personZid) ? report.getString("person_id") : personZid;
    personZid = StringUtils.isBlank(personZid) ? report.getString("patient_id") : personZid;
    personZid = StringUtils.isBlank(personZid) ? _personZid : personZid;
    if (StringUtils.isBlank(personZid)) {
      System.err.println("错误 ==>: 患者ID为空！");
      EventLoop.asyncIO(() -> System.exit(0), 5000);
      return;
    }

    for (String measurement : tables) {
      CountInfo countInfo = influxTemplate.queryCountInfo(measurement, "*", startTime, endTime);
      if (countInfo.getCount() > 10) {
        try (final FileWriterImpl writer = IWriter.create(dir + measurement + "." + opts.type.toLowerCase(), false);) {
          String sql = "SELECT * FROM " + measurement + " WHERE"
              + " time >= '" + DateFmtter.fmtUtc(startTime) + "'"
              + " AND time < '" + DateFmtter.fmtUtc(endTime) + "'"
              + " AND (person_zid = '" + personZid + "' OR patient_id = '" + personZid + "')";
          log.info("sql: {}, startAt: {}, endAt: {}", sql, DateFmtter.fmt(startTime), DateFmtter.fmt(endTime));
          //System.err.println(String.format("sql: %s, startAt: %s, endAt: %s", sql, DateFmtter.fmt(startTime), DateFmtter.fmt(endTime)));
          Set<String> tagKeys = new HashSet<>(influxTemplate.getTagKeys(measurement));
          influxTemplate.query(sql, 100)
              .subscribe(new QueryObserver() {
                @Override
                public void onSeriesNext(List<Object> values, ValueConverter c, int position) {
                  if (opts.type.equalsIgnoreCase("json")) {
                    JSONObject json = new JSONObject(new LinkedHashMap<>());
                    Map<String, String> tags = new HashMap<>();
                    Map<String, Object> fields = new HashMap<>();
                    for (String column : c.getColumns()) {
                      if (column.equals("time")) continue;
                      if (tagKeys.contains(column)) {
                        String tagValue = c.getString(column, null);
                        if (tagValue != null) {
                          tags.put(column, tagValue);
                        }
                      } else {
                        fields.put(column, c.getValue(column, null));
                      }
                    }
                    json.put("time", c.getTime());
                    json.put("tags", tags);
                    json.put("fields", fields);
                    writer.writeAndFlush(json.toString()).writeAndFlush("\n");
                  } else {
                    LineProtocol lp = new LineProtocol(c.getName(), c.getTime(), TimeUnit.MILLISECONDS);
                    lp.getTags().putAll(c.getTags());
                    for (String column : c.getColumns()) {
                      if (column.equals("time")) continue;
                      if (tagKeys.contains(column)) {
                        // TAG
                        String tagValue = c.getString(column, null);
                        if (tagValue != null) {
                          lp.getTags().put(column, tagValue);
                        }
                      } else {
                        // 字段
                        lp.getFields().put(column, c.getValue(column, null));
                      }
                    }
                    writer.writeAndFlush(lp.toLineProtocol()).writeAndFlush("\n");// 转换成行协议
                  }
                }
              });
          writer.flush();
          if (writer.length() < 10) {
            IOUtils.delete(writer.source());
          }
        }
      }
    }

    System.exit(0);
  }


  @SuperBuilder
  @NoArgsConstructor
  @Data
  @ConfigurationProperties(prefix = "export")
  @Component
  public static class Options {
    /**
     * 导出类型: json、line
     */
    @Builder.Default
    String type = "line";
    /**
     * 存储的目录，默认./data/
     */
    @Builder.Default
    String destDir = "./data/";
    /**
     * guard_info, hs_alarm, hs_all_rates, hs_arrhythmia_alarm, hs_base_package, hs_blood, hs_darma_mattress, hs_ecg_wave, hs_iew_alarm, hs_location, hs_loss_package, hs_mattress_package, hs_offline_algo, hs_patient_alarm, hs_realtime_alarm_record, hs_resp_alarm, hs_resp_filter, hs_resp_xyz_wave, hs_routines_alarm, hs_spo2_wave, hs_sport_point, hs_svm_wave, hs_teleecg_wave_package, hs_teleecg_wave_point, hs_temperature, hs_tidal_volume, hs_tidal_volume_wave, hs_trend_rates, hs_wave_package, hs_wave_point, hs_wit_point, trend_back_data
     * <p>
     * 导出的表，逗号分割，如果是全部包，可以用*代替
     */
    @Builder.Default
    String tables = "hs_all_rates,hs_base_package,hs_wave_package,hs_alarm,hs_teleecg_wave_package,hs_temperature,hs_tidal_volume,hs_tidal_volume_wave,hs_blood,hs_darma_mattress";
    /**
     * 报告类型，必需，pr_report_data_6mwt、smwt、sleepStageAhi、sleepMattress
     */
    String reportType;
    /**
     * 报告ID，必需
     */
    String reportId;
    /**
     * 患者ID，非必需，优先从报告中获
     */
    String personZid;
    /**
     * 开始时间，非必需，优先从报告中获
     */
    String startTime;
    /**
     * 结束时间，非必需，优先从报告中获取
     */
    String endTime;
  }

}
