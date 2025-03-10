package com.benefitj.dataplatform.pg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface DatabaseHelper {

  /**
   * 数据库连接
   */
  Connection getConnection();

  /**
   * 创建 Statement
   */
  default Statement createStatement() {
    try {
      return getConnection().createStatement();
    } catch (SQLException e) {
      throw new MySQLException(e);
    }
  }

  /**
   * 创建 PreparedStatement
   */
  default PreparedStatement prepareStatement(String sql) {
    try {
      return getConnection().prepareStatement(sql);
    } catch (SQLException e) {
      throw new MySQLException(e);
    }
  }


  /**
   * 判断数据库是否存在
   */
  boolean existDatabase(String dbName);

  /**
   * 创建数据库
   */
  boolean createDatabase(String dbName);

  /**
   * 获取数据库名称
   */
  List<String> getDatabases();

  /**
   * 是否存在表
   */
  boolean existTable(String tableName);

  /**
   * 获取表名和字段
   */
  List<TableInfo> getTables();

  /**
   * 获取表的注释
   */
  String getTableComment(String tableName);

}
