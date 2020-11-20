package com.benefitj.spring.influxdb.dto;

import org.influxdb.annotation.Column;

/**
 * 持续查询
 */
public class ContinuousQuery {

  /**
   * 数据库名
   */
  private String database;

  @Column(name = "name")
  private String name;
  /**
   * 执行
   */
  @Column(name = "query")
  private String query;

  public ContinuousQuery() {
  }

  public ContinuousQuery(String database, String name, String query) {
    this.database = database;
    this.name = name;
    this.query = query;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }
}
