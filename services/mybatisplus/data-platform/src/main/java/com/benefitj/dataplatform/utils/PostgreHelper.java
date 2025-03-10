package com.benefitj.dataplatform.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Slf4j
public class PostgreHelper {

//  static final SingletonSupplier<PostgreHelper> singleton = SingletonSupplier.of(PostgreHelper::new);
//
//  public static PostgreHelper get() {
//    return singleton.get();
//  }


  /**
   * 获取数据库名称
   */
  public static List<String> getDatabases(Connection conn) {
    // 创建 Statement 对象
    try (Statement statement = conn.createStatement()) {
      // 执行查询
      try (ResultSet rs = statement.executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false")) {
        List<String> databases = new LinkedList<>();
        // 遍历结果集
        while (rs.next()) {
          String databaseName = rs.getString("datname");
          databases.add(databaseName);
        }
        return databases;
      }
    } catch (SQLException e) {
      throw new PSQLException(e);
    }
  }

  /**
   * 获取表名和字段
   */
  public static Map<String, List<ColumnInfo>> getTables(Connection conn, String db) {
    // 查询 public schema 下的所有表
    try (Statement stmt = conn.createStatement();) {
      if(StringUtils.isNotBlank(db)) stmt.execute("\\c " + db +";");
      try(ResultSet rs = stmt.executeQuery("SELECT table_name" +
              " FROM information_schema.tables" +
              " WHERE table_schema = 'public';")) {
        Map<String, List<ColumnInfo>> tables = new LinkedHashMap<>();
        while (rs.next()) {
          String tableName = rs.getString("table_name");
          // 查询该表的所有字段及其数据类型
          try (PreparedStatement pstmt = conn.prepareStatement("SELECT *" +
              " FROM information_schema.columns" +
              " WHERE table_schema = 'public' AND table_name = ?")) {
            pstmt.setString(1, tableName);
            try (ResultSet crs = pstmt.executeQuery();) {
              ResultSetMetaData metaData = crs.getMetaData();
              List<ColumnInfo> columns = new LinkedList<>();
              while (crs.next()) {
                JSONObject column = new JSONObject();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                  column.put(metaData.getColumnName(i), crs.getObject(i));
                }
                columns.add(column.toJavaObject(ColumnInfo.class));
              }
              tables.put(tableName, columns);
            }
          }
        }
        return tables;
      }


    } catch (SQLException e) {
      throw new PSQLException(e);
    }
  }

}
