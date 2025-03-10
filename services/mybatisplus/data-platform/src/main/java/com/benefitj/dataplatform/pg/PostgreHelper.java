package com.benefitj.dataplatform.pg;

import com.alibaba.fastjson2.JSONObject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PostgreHelper implements DatabaseHelper {

//  static final SingletonSupplier<PostgreHelper> singleton = SingletonSupplier.of(PostgreHelper::new);
//
//  public static PostgreHelper get() {
//    return singleton.get();
//  }

  DataSource dataSource;

  public PostgreHelper() {
  }

  public PostgreHelper(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Connection getConnection() {
    try {
      return getDataSource().getConnection();
    } catch (java.sql.SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public boolean existDatabase(String dbName) {
    try (PreparedStatement stmt = getConnection().prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
      stmt.setString(1, dbName);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    } catch (java.sql.SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public boolean createDatabase(String dbName) {
    try (Statement stmt = getConnection().createStatement()) {
      // 创建数据库的 SQL 命令
      stmt.executeUpdate("CREATE DATABASE " + dbName);
      return true;
    } catch (java.sql.SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public List<String> getDatabases() {
    // 创建 Statement 对象
    try (Statement stmt = getConnection().createStatement();
         ResultSet rs = stmt.executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false")) {
      // 执行查询
      List<String> databases = new LinkedList<>();
      while (rs.next()) databases.add(rs.getString("datname"));
      return databases;
    } catch (java.sql.SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public boolean existTable(String tableName) {
    String schemaName = "public"; // 默认模式是 public
    try (PreparedStatement stmt = getConnection().prepareStatement("SELECT 1 FROM pg_tables WHERE tablename = ? AND schemaname = ?")) {
      stmt.setString(1, tableName);
      stmt.setString(2, schemaName);
      try (ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    } catch (java.sql.SQLException e) {
      throw new MySQLException(e);
    }
  }


  @Override
  public Map<String, List<ColumnInfo>> getTables() {
    // 查询 public schema 下的所有表
    Connection conn = getConnection();
    try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT table_name" + " FROM information_schema.tables" + " WHERE table_schema = 'public';")) {
      Map<String, List<ColumnInfo>> tables = new LinkedHashMap<>();
      while (rs.next()) {
        String tableName = rs.getString("table_name");
        // 查询该表的所有字段及其数据类型
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT *" + " FROM information_schema.columns" + " WHERE table_schema = 'public' AND table_name = ?")) {
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
    } catch (java.sql.SQLException e) {
      throw new MySQLException(e);
    }
  }

}
