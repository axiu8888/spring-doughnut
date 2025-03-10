package com.benefitj.spring.datasource;

/**
 * 数据库类型
 */
public enum DbType {

  PG("postgresql"),
  MYSQL("mysql");

  private final String value;

  DbType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

}
