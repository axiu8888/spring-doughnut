package com.benefitj.spring.freemarker.jdbc;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JdbcType {

  /**
   * JDBC的类型
   */
  private String jdbc;
  /**
   * Java 类型
   */
  private String java;
  /**
   * 长度
   */
  private Integer length;

  public Class<?> getJavaClass() {
    try {
      return Class.forName(getJava());
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  static {
    Map<String, JdbcType> templates = new LinkedHashMap<>();
    Arrays.asList(JdbcType.builder()
            .jdbc("varchar")
            .java(String.class.getName())
            .length(255)
            .build()
        , JdbcType.builder()
            .jdbc("char")
            .java(String.class.getName())
            .length(255)
            .build()
        , JdbcType.builder()
            .jdbc("tinytext")
            .java(String.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("mediumtext")
            .java(String.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("text")
            .java(String.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("longtext")
            .java(String.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("tinyint")
            .java(Integer.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("mediumint")
            .java(Integer.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("int")
            .java(Integer.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("bigint")
            .java(Long.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("date")
            .java(Date.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("datetime")
            .java(Date.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("datetime")
            .java(Date.class.getName())
            .build()
        , JdbcType.builder()
            .jdbc("json")
            .java(String.class.getName())
            .build()
    )
    .forEach(jdbcType -> templates.put(jdbcType.jdbc, jdbcType));


  }

}
