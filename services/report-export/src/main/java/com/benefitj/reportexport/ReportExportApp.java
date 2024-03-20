package com.benefitj.reportexport;

import cn.hutool.json.JSONObject;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.core.file.FileWriterImpl;
import com.benefitj.core.file.IWriter;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.influxdb.spring.EnableInfluxDB;
import com.benefitj.spring.influxdb.template.InfluxTemplate;
import com.benefitj.spring.influxdb.template.QueryObserver;
import com.benefitj.spring.influxdb.template.ValueConverter;
import com.benefitj.spring.listener.AppStateHook;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@EnableInfluxDB
@SpringBootApplication
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

    String[] tables = SpringCtxHolder.getEnvProperty("tables").split(",");
    String reportType = SpringCtxHolder.getEnvProperty("reportType");
    String reportId = SpringCtxHolder.getEnvProperty("reportId");
    String personZid = SpringCtxHolder.getEnvProperty("personZid");
    long startTime = DateFmtter.parseToLong(SpringCtxHolder.getEnvProperty("startTime"));
    long endTime = DateFmtter.parseToLong(SpringCtxHolder.getEnvProperty("endTime"));
    String dir = SpringCtxHolder.getEnvProperty("destDir", "./data") + "/";

    if (StringUtils.isAnyBlank(reportId, reportType)) {
      System.err.println("错误 ==>: 请配置报告ID和报告类型！");
      EventLoop.asyncIO(() -> System.exit(0), 5000);
      return;
    }

    JSONObject report = mongoTemplate.findById(reportId, JSONObject.class, reportType);
    if (report == null) {
      System.err.println("错误 ==>: 无法查找到对应报告！");
      EventLoop.asyncIO(() -> System.exit(0), 5000);
      return;
    }
    if (SpringCtxHolder.getEnvProperty("tables", "").trim().equals("*")) {
      tables = influxTemplate.getMeasurements().toArray(new String[0]);
    }

    String personName = report.getStr("personName");
    personName = StringUtils.isBlank(personName) ? report.getStr("person_name") : personName;
    personName = StringUtils.isBlank(personName) ? "" : personName;
    dir = dir + personName + "__" + reportId + "/";
    String task_startTime = report.getStr("task_startTime");
    task_startTime = StringUtils.isBlank(task_startTime) ? report.getStr("startTime") : task_startTime;
    task_startTime = StringUtils.isBlank(task_startTime) ? report.getStr("start_time") : task_startTime;
    String task_endTime = report.getStr("task_endTime");
    task_endTime = StringUtils.isBlank(task_endTime) ? report.getStr("endTime") : task_endTime;
    task_endTime = StringUtils.isBlank(task_endTime) ? report.getStr("end_time") : task_endTime;
    startTime = StringUtils.isNotBlank(task_startTime) ? DateFmtter.parseToLong(task_startTime) : startTime;
    endTime = StringUtils.isNotBlank(task_endTime) ? DateFmtter.parseToLong(task_endTime) : endTime;
    try (final IWriter w = IWriter.createWriter(IOUtils.createFile(dir + "report.json"), false)) {
      w.writeAndFlush(report.toString());
    }

    String _personZid = personZid;
    personZid = StringUtils.isBlank(personZid) ? report.getStr("personZid") : personZid;
    personZid = StringUtils.isBlank(personZid) ? report.getStr("person_zid") : personZid;
    personZid = StringUtils.isBlank(personZid) ? report.getStr("person_id") : personZid;
    personZid = StringUtils.isBlank(personZid) ? report.getStr("patient_id") : personZid;
    personZid = StringUtils.isBlank(personZid) ? _personZid : personZid;
    if (StringUtils.isBlank(personZid)) {
      System.err.println("错误 ==>: 患者ID为空！");
      EventLoop.asyncIO(() -> System.exit(0), 5000);
      return;
    }

    for (String measurement : tables) {
      if (influxTemplate.queryCountInfo(measurement, "*", startTime, endTime).getCount() > 10) {
        try (final FileWriterImpl writer = (FileWriterImpl) IWriter.createWriter(dir + measurement + ".json", false);) {
          String sql = "select * from " + measurement + " where"
              + " time >= '" + DateFmtter.fmtUtc(startTime) + "'"
              + " AND time < '" + DateFmtter.fmtUtc(endTime) + "'"
              + " AND (person_zid = '" + personZid + "' OR patient_id = '" + personZid + "')";
          log.info("sql: {}, startAt: {}, endAt: {}", sql, DateFmtter.fmt(startTime), DateFmtter.fmt(endTime));
          //System.err.println(String.format("sql: %s, startAt: %s, endAt: %s", sql, DateFmtter.fmt(startTime), DateFmtter.fmt(endTime)));
          influxTemplate.query(sql, 100)
              .subscribe(new QueryObserver() {
                @Override
                public void onSeriesNext(List<Object> values, ValueConverter c, int position) {
                  JSONObject json = new JSONObject(new LinkedList<>());
                  json.set("time", c.getTime());
                  for (String column : c.getColumns()) {
                    json.set(column, c.getValue(column, null));
                  }
                  writer.writeAndFlush(json.toString());
                  writer.writeAndFlush("\n");
                }
              });
          writer.flush();
          if (writer.length() < 10) {
            IOUtils.deleteFiles(writer.source());
          }
        }
      }
    }

    System.exit(0);
  }

}
