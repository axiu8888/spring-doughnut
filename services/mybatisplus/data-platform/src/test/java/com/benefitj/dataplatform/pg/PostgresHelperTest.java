package com.benefitj.dataplatform.pg;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.benefitj.core.IOUtils;
import com.benefitj.core.functions.Pair;
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
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
  void test_classToTable() {
    SQLGenerator sqlGenerator = SQLGenerator.get();
//    pgHelper.createDatabase("", )
    File dir = new File("D:/develop/.tmp/cache/query/report_classes");
    List<Pair<String, JSONArray>> classes = IOUtils.listFiles(dir, f -> true,false)
        .stream()
        .filter(File::isFile)
        .filter(f -> f.getName().endsWith(".json"))
        .map(f -> Pair.of(f.getName(), JSON.parseArray(IOUtils.readAsString(f))))
        .collect(Collectors.toList());
    for (Pair<String, JSONArray> pair : classes) {
      String fullName = pair.getKey();
      int endIndex = fullName.lastIndexOf(".");
      int startIndex = fullName.lastIndexOf(".", endIndex - 1);
      String className = fullName.substring(startIndex + 1, endIndex);
      String version = fullName.substring(fullName.lastIndexOf(".", startIndex - 1) + 1, startIndex);
      version = !(version.startsWith("v") && StringUtils.isNumeric(version.substring(1))) ? "" : version;
      log.info("--------->: {}{}", className, StringUtils.isNotBlank(version) ? "_" + version : "");

      String tableName = "auto_query_" + camelToSnake(className.replace("ReportEntity", "")) + (StringUtils.isNotBlank(version) ? "_" + version : "");
      List<ColumnDefine> columns = pair.getValue()
          .stream()
          .map(json -> (JSONObject) json)
          .map(json -> json.toJavaObject(FieldDescriptor.class))
          .map(fd -> ColumnDefine.builder()
              //.columnName(camelToSnake(fd.name))
              .columnName(fd.name)
              .dataType(fd.type)
              .isNullable(fd.name.equals("orgId") || fd.name.equals("org_id") ? "NO" : "YES")
              .primaryKey(fd.name.equals("id") || fd.name.equals("_id"))
              .characterMaximumLength(isId(fd.name) ? 32 : null)
              .columnComment(fd.note)
              .build())
          .collect(Collectors.toList());

      TableDefine table = TableDefine.builder()
          .tableName(tableName)
          .tableComment(className)
          .columns(columns)
          .build();

      String tableSQL = SQLGenerator.get().createTable(table.getTableName(), table.getColumns());
      String tableCommentSQL = SQLGenerator.get().tableComment(table.getTableName(), table.getTableComment());
      String columnCommentSQL = SQLGenerator.get().columnsComment(table.getTableName(), table.getColumns());
//      log.info("{} tableSQL -->: \n{}", tableName, tableSQL);
//      log.info("{} tableCommentSQL -->: \n{}", tableName, tableCommentSQL);
//      log.info("{} columnCommentSQL -->: \n{}", tableName, columnCommentSQL);

      int row = pgHelper.createTable(table);
      log.info("row -->: {}", row);

    }
  }

  public static boolean isId(String name) {
    return name.endsWith("id")
        || name.endsWith("_id")
        || name.endsWith("Id")
        || name.endsWith("_zid")
        || name.endsWith("Zid");
  }

  public static String camelToSnake(String str) {
    if (StringUtils.isBlank(str)) return str;
    // 使用正则表达式替换大写字母
    return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
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
          String tableSQL = SQLGenerator.get().createTable(table.getTableName(), table.getColumns());
          String commentSQL = SQLGenerator.get().columnsComment(table.getTableName(), table.getColumns());
          log.info("{} -->: \n{}{}", tableName, tableSQL, StringUtils.isNotBlank(commentSQL) ? "\n" + commentSQL : "");
          IOUtils.write((tableSQL + "\n\n" + commentSQL).getBytes(StandardCharsets.UTF_8)
              , IOUtils.createFile(dir, "tables_sql/" + table.getTableName() + ".sql"));
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