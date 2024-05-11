package com.benefitj.mybatisplus;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IOUtils;
import com.benefitj.mybatisplus.dao.mapper.MysqlMapper;
import com.benefitj.mybatisplus.dao.mapper.PostgresqlMapper;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.listener.EnableAppStateListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@EnableAppStateListener
@PropertySource(value = {"classpath:version.properties"}, encoding = "utf-8")
@SpringBootApplication
@Slf4j
public class DCMybatisPlusApp {
  public static void main(String[] args) {
    SpringApplication.run(DCMybatisPlusApp.class, args);
  }

  static {
    AppStateHook.registerStart(evt -> appStart());
    AppStateHook.registerStop(evt -> appStop());
  }

  public static void appStart() {
    try {
      log.info("app start...");

      if (!Boolean.parseBoolean(SpringCtxHolder.getEnvProperty("statistics.enable"))) return;

      PostgresqlMapper postgresqlMapper = SpringCtxHolder.getBean(PostgresqlMapper.class);
      MysqlMapper mysqlMapper = SpringCtxHolder.getBean(MysqlMapper.class);

      File outDir = new File(SpringCtxHolder.getEnvProperty("statistics.outDir", "./tmp/statistics"));
      String statisticsPath = SpringCtxHolder.getEnvProperty("statistics.path");
      File statisticsFile = new File(statisticsPath);
      if (!statisticsFile.exists()) {
        statisticsFile = new ClassPathResource(statisticsPath).getFile();
      }
      JSONObject conf = JSON.parseObject(IOUtils.readAsString(statisticsFile));
      Date startDate = StringUtils.isNotBlank(conf.getString("startDate")) ? DateFmtter.parse(conf.getString("startDate"), DateFmtter._yMd) : null;
      Date endDate = StringUtils.isNotBlank(conf.getString("endDate")) ? DateFmtter.parse(conf.getString("endDate"), DateFmtter._yMd) : null;
      conf.getJSONObject("category").forEach((key, value) -> {
        JSONArray items = (JSONArray) value;
        List<JSONObject> list = mysqlMapper.countByItems(
                items.stream().map(String::valueOf).toArray(String[]::new),
                startDate,
                endDate
            )
            .stream()
            .peek(json -> {
              json.put("type", key); // 六分钟
              json.put("source", "znsx"); // 智能随行系统
            })
            .collect(Collectors.toList());
        IOUtils.write(
            JSON.toJSONString(list, JSONWriter.Feature.PrettyFormat).getBytes(StandardCharsets.UTF_8),
            IOUtils.createFile(outDir, "mysql_" + key + ".json"));
        //log.info("{}List ==>: \n{}\nitems ==>: '{}'\n", key, JSON.toJSONString(list), items.stream().map(String::valueOf).collect(Collectors.joining("', '")));
      });
      EventLoop.asyncIO(() -> System.exit(0), 1000);
    } catch (Exception e) {
      log.error("throws ==>: " + e.getStackTrace(), e);
    }
  }

  public static void appStop() {
  }


}
