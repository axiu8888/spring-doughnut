package com.benefitj.dataplatform.pg;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface DatabaseHelper {

  /**
   * 数据库连接
   */
  Connection getConnection();

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
  Map<String, List<ColumnInfo>> getTables();

}
