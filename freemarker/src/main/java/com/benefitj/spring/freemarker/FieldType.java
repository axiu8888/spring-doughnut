package com.benefitj.spring.freemarker;

import java.sql.Timestamp;
import java.util.Date;

public enum FieldType {

  /**
   * 字节
   */
  BYTE("Byte", Byte.class, "字节"),
  /**
   * 短整型
   */
  SHORT("Short", Short.class, "短整型"),
  /**
   * 整型
   */
  INTEGER("Integer", Integer.class, "整型"),
  /**
   * 长整型
   */
  LONG("Long", Long.class, "长整型"),
  /**
   * 单精度浮点数
   */
  FLOAT("Float", Float.class, "单精度浮点数"),
  /**
   * 浮点数
   */
  DOUBLE("Double", Double.class, "浮点数"),
  /**
   * 布尔
   */
  BOOLEAN("Boolean", Boolean.class, "布尔"),
  /**
   * 字符串
   */
  STRING("String", String.class, "字符串"),
  /**
   * 日期
   */
  DATE("Date", Date.class, "日期"),
  /**
   * 时间戳
   */
  TIMESTAMP("Timestamp", Timestamp.class, "时间戳"),
  /**
   * char
   */
  CHAR("Char", Character.class, "字符"),
  /**
   * 自定义
   */
  CUSTOM(null, null, "自定义"),
  ;

  final String name;
  final Class<?> type;
  final String description;

  FieldType(String name, Class<?> type, String description) {
    this.name = name;
    this.type = type;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public Class<?> getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public static FieldType of(String name) {
    for (FieldType v : values()) {
      if (v.name.equalsIgnoreCase(name)) {
        return v;
      }
    }
    return CUSTOM;
  }

}
