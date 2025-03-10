package com.benefitj.dataplatform;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.IOUtils;
import com.benefitj.dataplatform.pg.PostgreHelper;
import com.benefitj.dataplatform.pg.TableInfo;
import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.listener.EnableAppStateListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


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
      //PostgreHelper pgHelper = new PostgreHelper(SpringCtxHolder.getBean(DruidDataSource.class));
      PostgreHelper pgHelper = new PostgreHelper(SpringCtxHolder.getBean(DataSource.class));
      if (!pgHelper.existDatabase("support_pr")) {
        log.info("数据库 support_pr 创建成功： {}", pgHelper.createDatabase("support_pr"));
      }
      List<String> databases = pgHelper.getDatabases();
      log.info("databases ==>: {}", JSON.toJSONString(databases));
      log.info("hs_report_task existTable ==>: {}", pgHelper.existTable("hs_report_task"));


      // 要创建的表名称和结构
      String tableName = "employees";
      if (!pgHelper.existTable(tableName)) {
        String tableStructure = "id SERIAL PRIMARY KEY, " +
            "name VARCHAR(100) NOT NULL, " +
            "age INT, " +
            "salary NUMERIC(10, 2)";
        // 动态生成 CREATE TABLE 语句
        String createTableSQL = "CREATE TABLE " + tableName + " (" + tableStructure + ")";
        try (Statement statement = pgHelper.createStatement()) {
          // 执行创建表的 SQL 语句
          statement.executeUpdate(createTableSQL);
          System.err.println("创建表成功: " + tableName);

          // 表注释和列注释
          String tableComment = "员工信息表";
          String idComment = "员工ID";
          String nameComment = "员工姓名";
          String ageComment = "员工年龄";
          String salaryComment = "员工薪资";

          // 添加表注释
          String tableCommentSQL = "COMMENT ON TABLE " + tableName + " IS '" + tableComment + "'";
          statement.executeUpdate(tableCommentSQL);
          System.out.println("表注释添加成功！");

          // 添加列注释
          String idCommentSQL = "COMMENT ON COLUMN " + tableName + ".id IS '" + idComment + "'";
          String nameCommentSQL = "COMMENT ON COLUMN " + tableName + ".name IS '" + nameComment + "'";
          String ageCommentSQL = "COMMENT ON COLUMN " + tableName + ".age IS '" + ageComment + "'";
          String salaryCommentSQL = "COMMENT ON COLUMN " + tableName + ".salary IS '" + salaryComment + "'";

          statement.executeUpdate(idCommentSQL);
          statement.executeUpdate(nameCommentSQL);
          statement.executeUpdate(ageCommentSQL);
          statement.executeUpdate(salaryCommentSQL);
          System.out.println("列注释添加成功！");
          //return true;
        } catch (SQLException e) {
          System.err.println("创建表失败: " + e.getMessage());
        }
      }

      List<TableInfo> tables = pgHelper.getTables();
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
