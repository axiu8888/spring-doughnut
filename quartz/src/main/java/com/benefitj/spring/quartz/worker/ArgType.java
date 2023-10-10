package com.benefitj.spring.quartz.worker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * 参数类型
 */
@ApiModel("参数类型")
public enum ArgType {

  @ApiModelProperty("整数")
  INTEGER(List.of(byte.class, Byte.class, short.class, Short.class, int.class, Integer.class, long.class, Long.class)),

  @ApiModelProperty("浮点数")
  FLOAT(List.of(float.class, Float.class, double.class, Double.class, BigDecimal.class)),

  @ApiModelProperty("布尔")
  BOOLEAN(List.of(boolean.class, Boolean.class)),

  @ApiModelProperty("字符串")
  STRING(List.of(String.class, CharSequence.class)),

  @ApiModelProperty("日期: yyyy-MM-dd、yyyy-MM-dd HH:mm:ss")
  DATE(List.of(Date.class, Instant.class, Timestamp.class)),

  @ApiModelProperty("JSON")
  JSON(List.of(Object.class)),

  @ApiModelProperty("JSONArray")
  JSON_ARRAY(List.of(Object[].class)),
  ;

  final List<Class> types;

  ArgType(List<Class> types) {
    this.types = types;
  }

  public static ArgType find(Class<?> type) {
    if (type.isArray()) {
      return JSON_ARRAY;
    }
    for (ArgType value : values()) {
      for (Class cls : value.types) {
        if (cls.isAssignableFrom(type)) {
          return value;
        }
      }
    }
    return JSON;
  }


}
