package com.benefitj.dataplatform;

import com.benefitj.core.IOUtils;
import com.benefitj.dataplatform.utils.ColumnInfo;
import com.benefitj.dataplatform.utils.PostgreHelper;
import com.benefitj.spring.JsonUtils;
import com.benefitj.spring.listener.AppStateHook;
import com.benefitj.spring.listener.EnableAppStateListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.sql.Connection;
import java.sql.DriverManager;
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
//    AppStateHook.registerStop(evt -> appStop());
  }

  public static void appStart() {
    try {
      log.info("appStart -------------------------------------------------------> start");
      // 数据库连接信息，请根据实际情况修改
//      String url = "jdbc:postgresql://192.168.1.194:55432/data_platform";
      String url = "jdbc:postgresql://192.168.1.194:55432/";
      String user = "postgres";
      String password = "hsrg8888";

      try (final Connection conn = DriverManager.getConnection(url, user, password)) {
        Map<String, List<ColumnInfo>> tables = PostgreHelper.getTables(conn, "support_pr");
//        Map<String, List<ColumnInfo>> tables = PostgreHelper.getTables(conn, "");
        IOUtils.write(JsonUtils.toJsonBytes(tables, true), IOUtils.createFile("D:/tmp/cache/data/tables.json"));
        //log.info("tables ==>: \n{}", JSON.toJSONString(tables));


//        // 查询
//        try (Statement statement = conn.createStatement()) {
//          // 执行查询
//          ResultSet crs = statement.executeQuery("SELECT *" +
//              " FROM information_schema.columns" +
//              " WHERE table_schema = 'public' AND table_name = 'hs_report_task'");
//
//          // 获取 ResultSetMetaData
//          ResultSetMetaData metaData = crs.getMetaData();
//
//          // 获取查询结果的字段信息
//          int columnCount = metaData.getColumnCount();
//          System.out.println("查询结果的字段信息：");
//          for (int i = 1; i <= columnCount; i++) {
//            String columnName = metaData.getColumnName(i); // 列名
//            String columnType = metaData.getColumnTypeName(i); // 列类型
//            System.out.println("列名: " + columnName + ", 类型: " + columnType);
//          }
//          // 遍历查询结果
//          System.out.println("\n查询结果：");
//          while (crs.next()) {
//            Map<String, Object> column = new LinkedHashMap<>();
//            for (int i = 1; i <= columnCount; i++) {
//              String columnName = metaData.getColumnName(i);
//              Object value = crs.getObject(i); // 获取列值
//              System.out.println(columnName + ": " + value);
//              column.put(columnName, value);
//            }
//            System.out.println("column ==>: " + JsonUtils.toJson(column));
//            System.out.println("-----------------------------\n");
//          }
//        }
      }
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
