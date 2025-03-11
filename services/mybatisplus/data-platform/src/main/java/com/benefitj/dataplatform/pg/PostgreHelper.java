package com.benefitj.dataplatform.pg;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;


@Slf4j
public class PostgreHelper implements DatabaseHelper {

  String schemaName = "public";
  DataSource dataSource;

  public PostgreHelper() {
  }

  public PostgreHelper(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public PostgreHelper(String schemaName, DataSource dataSource) {
    this.schemaName = schemaName;
    this.dataSource = dataSource;
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
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
    } catch (SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public boolean existDatabase(String dbName) {
    try (final PreparedStatement stmt = prepareStatement(getConnection(), "SELECT 1 FROM pg_database WHERE datname = ?")) {
      stmt.setString(1, dbName);
      try (final ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public boolean createDatabase(String dbName) {
    try (final Statement stmt = createStatement(getConnection())) {
      // 创建数据库的 SQL 命令
      stmt.executeUpdate("CREATE DATABASE " + dbName);
      return true;
    } catch (SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public List<String> getDatabases() {
    Connection conn = getConnection();
    try (final Statement stmt = createStatement(conn);
         ResultSet rs = stmt.executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false")) {
      // 执行查询
      List<String> databases = new LinkedList<>();
      while (rs.next()) databases.add(rs.getString("datname"));
      return databases;
    } catch (SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public boolean existTable(String tableName) {
    Connection conn = getConnection();
    String schemaName = getSchemaName(); // 默认模式是 public
    try (final PreparedStatement stmt = prepareStatement(conn, "SELECT 1 FROM pg_tables WHERE tablename = ? AND schemaname = ?")) {
      stmt.setString(1, tableName);
      stmt.setString(2, schemaName);
      try (final ResultSet rs = stmt.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public List<TableInfo> getTables() {
    // 查询 public schema 下的所有表
    String schemaName = getSchemaName();
    Connection conn = getConnection();
    try (final Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT table_name" + " FROM information_schema.tables" + " WHERE table_schema = '"+ schemaName +"';")) {
      List<TableInfo> tables = new LinkedList<>();
      while (rs.next()) {
        String tableName = rs.getString("table_name");
        // 查询该表的所有字段及其数据类型
        try (final PreparedStatement pstmt = conn.prepareStatement("SELECT *" + " FROM information_schema.columns" + " WHERE table_schema = ? AND table_name = ?")) {
          pstmt.setString(1, schemaName);
          pstmt.setString(2, tableName);
          List<ColumnInfo> columns = new LinkedList<>();
          try (final ResultSet crs = pstmt.executeQuery();) {
            ResultSetMetaData metaData = crs.getMetaData();
            while (crs.next()) {
              JSONObject json = new JSONObject();
              int columnCount = metaData.getColumnCount();
              for (int i = 1; i <= columnCount; i++) {
                json.put(metaData.getColumnName(i), crs.getObject(i));
              }
              columns.add(json.toJavaObject(ColumnInfo.class));
            }
          }
          /* -- 此处有问题，暂时注释掉
          // 查询列注释
          try (final PreparedStatement cpstmt = prepareStatement(conn, "SELECT col_description(?::regclass, ?)")) {
            for (ColumnInfo column : columns) {
              cpstmt.setString(1, schemaName + "." + tableName);
              cpstmt.setInt(2, column.getOrdinalPosition());
              try (final ResultSet crs2 = cpstmt.executeQuery()) {
                if (crs2.next()) {
                  column.setComment(crs2.getString(1));
                }
              }
            }
          }
          */
          tables.add(TableInfo.builder()
              .name(tableName)
              .columns(columns)
              .build());
        }
      }
      return tables;
    } catch (java.sql.SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public String getTableComment(String tableName) {
    String schemaName = getSchemaName(); // 默认模式是 public
    try (final PreparedStatement pstmt = prepareStatement(getConnection(), "SELECT obj_description(?::regclass, 'pg_class')")) {
      pstmt.setString(1, schemaName + "." + tableName);
      try (final ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) return rs.getString(1);
      }
      return null;
    } catch (SQLException e) {
      throw new MySQLException(e);
    }
  }

  @Override
  public JSONObject getColumnComments(String tableName, String ...columns) {
    JSONObject commentJson = new JSONObject(columns.length);
    String schemaName = getSchemaName(); // 默认模式是 public
    Connection conn = getConnection();
    // 获取表的列信息
    String sql = "SELECT column_name, ordinal_position FROM information_schema.columns WHERE table_schema = ? AND table_name = ? AND column_name IN(?)";
    try (final PreparedStatement pstmt = prepareStatement(conn, sql)) {
      pstmt.setString(1, schemaName);
      pstmt.setString(2, tableName);
      //pstmt.setString(3, "'" + String.join("', '", columns) + "'");
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          String columnName = rs.getString("column_name");
          int columnNumber = rs.getInt("ordinal_position");
          if (columnName == null) {
            continue;
          }
          // 查询列注释
          try (PreparedStatement cpsmt = conn.prepareStatement("SELECT col_description(?::regclass, ?)")) {
            cpsmt.setString(1, schemaName + "." + tableName);
            cpsmt.setInt(2, columnNumber);
            try (ResultSet crs = cpsmt.executeQuery()) {
              if (crs.next()) {
                String comment = crs.getString(1);
                commentJson.put(columnName, comment);
              } else {
                commentJson.put(columnName, "");
              }
            }
          }
        }
      }
      return commentJson;
    } catch (SQLException e) {
      throw new MySQLException(e);
    }
  }

}
