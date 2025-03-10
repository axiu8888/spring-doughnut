package com.benefitj.dataplatform;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.IOUtils;
import com.benefitj.dataplatform.pg.ColumnInfo;
import com.benefitj.dataplatform.pg.PostgreHelper;
import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.listener.EnableAppStateListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


@EnableAppStateListener
@PropertySource(value = {"classpath:version.properties"}, encoding = "utf-8")
@SpringBootApplication
@Slf4j
public class DataPlatformApp {
  public static void main(String[] args) {
    SpringApplication.run(DataPlatformApp.class, args);
  }

  static {
    AppStateHook.registerStart(evt -> appStart());
    //AppStateHook.registerStop(evt -> appStop());
  }

  public static void appStart() {
    try {
      log.info("appStart -------------------------------------------------------> start");
      // 数据库连接信息，请根据实际情况修改
      //PostgreHelper postgreHelper = new PostgreHelper(SpringCtxHolder.getBean(DruidDataSource.class));
      PostgreHelper postgreHelper = new PostgreHelper(SpringCtxHolder.getBean(DataSource.class));
      if (!postgreHelper.existDatabase("support_pr")) {
        log.info("数据库 support_pr 创建成功： {}", postgreHelper.createDatabase("support_pr"));
      }
      List<String> databases = postgreHelper.getDatabases();
      log.info("databases ==>: {}", JSON.toJSONString(databases));
      log.info("hs_report_task existTable ==>: {}", postgreHelper.existTable("hs_report_task"));
      Map<String, List<ColumnInfo>> tables = postgreHelper.getTables();
      IOUtils.write(JsonUtils.toJsonBytes(tables, true), IOUtils.createFile("D:/develop/.tmp/cache/tables.json"));
      //log.info("tables ==>: \n{}", JSON.toJSONString(tables));
    } catch (Exception e) {
      log.error("throws ==>: " + e.getStackTrace(), e);
    } finally {
      log.info("appStart -------------------------------------------------------> end");
      System.exit(0);
    }
  }

  public static void appStop() {
  }


}
