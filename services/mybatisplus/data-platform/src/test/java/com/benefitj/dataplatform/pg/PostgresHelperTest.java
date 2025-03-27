package com.benefitj.dataplatform.pg;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.IOUtils;
import com.benefitj.dataplatform.pg.dto.ColumnDefine;
import com.benefitj.dataplatform.pg.dto.TableDefine;
import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@SpringBootTest
class PostgresHelperTest {

  DataSource dataSource;
  PostgresHelper pgHelper;

  @BeforeEach
  void setUp() {
    if (dataSource == null) {
      dataSource = getDataSource(SpringCtxHolder.getBean(DataSourceProperties.class));
      pgHelper = new PostgresHelper(dataSource);
    }
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void testSQLGenerator() {
    SQLGenerator sqlGenerator = SQLGenerator.get();
    log.info("createDatabase -->: {}", sqlGenerator.createDatabase("support", "postgres", "postgres", "UTF-8"));
    log.info("tableComment -->: {}", sqlGenerator.tableComment("hs_person", "患者基本信息表"));

  }

  @Test
  void test() {
//    pgHelper.createDatabase("");
  }


  static DataSource getDataSource(DataSourceProperties properties) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(properties.getUrl());
    config.setUsername(properties.getUsername());
    config.setPassword(properties.getPassword());
    config.setDriverClassName(properties.getDriverClassName());

    // 高级配置
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setIdleTimeout(30000);
    config.setConnectionTimeout(2000);
    config.setLeakDetectionThreshold(5000);

    return new HikariDataSource(config);
  }

  @Test
  void test_sql() {
    try {
      log.info("appStart -------------------------------------------------------> start");
      // 数据库连接信息，请根据实际情况修改
      //PostgreHelper pgHelper = new PostgreHelper(SpringCtxHolder.getBean(DruidDataSource.class));
      //DataSource dataSource = SpringCtxHolder.getBean(DataSource.class);

      //DataSource dataSource = getDataSource(SpringCtxHolder.getBean(DataSourceProperties.class));
      //PostgresHelper pgHelper = new PostgresHelper(dataSource);
      if (!pgHelper.existDatabase("support")) {
        log.info("数据库 support_pr 创建成功： {}", pgHelper.createDatabase("support", "postgres", "postgres", "UTF-8"));
      }
      List<String> databases = pgHelper.getDatabases();
      log.info("databases ==>: {}", JSON.toJSONString(databases));
      log.info("hs_report_task existTable ==>: {}", pgHelper.existTable("hs_report_task") > 0);


      String dir = "D:/develop/.tmp/cache/query";
      // 要创建的表名称和结构
      String tableName = "hs_person";
      if (pgHelper.existTable(tableName) > 0) {
        List<TableDefine> tables = pgHelper.getTables(false);
        IOUtils.write(JsonUtils.toJsonBytes(tables, true), IOUtils.createFile(dir, "/tables.json"));
        //log.info("tables ==>: \n{}", JSON.toJSONString(tables));

        Set<String> ignores = new HashSet<>(Arrays.asList(
//            "hs_charging",
//            "hs_doc_pat",
            ""
        ));

        tables.forEach(table -> {
          String tableSQL = SQLGenerator.get().createTable(table.getName(), table.getColumns());
          String commentSQL = SQLGenerator.get().columnsComment(table.getName(), table.getColumns());
          log.info("{} -->: \n{}{}", tableName, tableSQL, StringUtils.isNotBlank(commentSQL) ? "\n" + commentSQL : "");
          IOUtils.write((tableSQL + "\n\n" + commentSQL).getBytes(StandardCharsets.UTF_8)
              , IOUtils.createFile(dir, "tables_sql/" + table.getName() + ".sql"));
        });

      }

      log.info("获取表的索引信息 --> \n{}", JSON.toJSONString(pgHelper.getIndexes("hs_person")));
      log.info("获取表的类型 --> \n{}", JSON.toJSONString(pgHelper.getTableType("hs_person")));

    } catch (Exception e) {
      log.error("throws ==>: " + e.getStackTrace(), e);
    } finally {
      log.info("appStart -------------------------------------------------------> end");
      //System.exit(0);
    }
  }
}