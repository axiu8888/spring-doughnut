package com.benefitj.dataplatform.pg;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 字段类型
 */
public enum FieldType {

  INT2("int2", "2字节整数(16位)", new Class[]{byte.class, short.class, Byte.class, Short.class}),
  INT4("int4", "4字节整数(32位)", new Class[]{int.class, Integer.class}),
  INT8("int8", "8字节整数(64位)", new Class[]{long.class, Long.class}),
  FLOAT("float", "单精度浮点数", new Class[]{float.class, Float.class}),
  DOUBLE("double", "双精度浮点数", new Class[]{double.class, Double.class}),
  DECIMAL("decimal", "高精度浮点数", new Class[]{BigDecimal.class}),
  BOOLEAN("int2", "布尔值", new Class[]{boolean.class, Boolean.class}),

  STRING("varchar", "字符串", new Class[]{String.class, StringBuilder.class, StringBuffer.class}) {
    @Override
    public boolean isType(Field field, Type type) {
      return super.isType(field, type);
    }
  },

  ENUM("varchar", "枚举", new Class[]{Enum.class}) {
    @Override
    public boolean isType(Field field, Type type) {
      if (type instanceof Class) {
        return ((Class<?>) type).isEnum();
      }
      return super.isType(field, type);
    }
  },

  DATETIME("datetime", "时间", new Class[]{java.util.Date.class, Timestamp.class, java.sql.Date.class, Instant.class}),

  JSON("json", "json", new Class<?>[]{Object.class, Map.class, com.alibaba.fastjson2.JSON.class}) {
    @Override
    public boolean isType(Field field, Type type) {
      if (super.isType(field, type)) {
        return true;
      }
      return false;
    }
  },

  ARRAY("jsonb", "数组", new Class<?>[]{Collection.class, List.class, Set.class, Object[].class, com.alibaba.fastjson2.JSONArray.class}) {
    @Override
    public boolean isType(Field field, Type type) {
      if (super.isType(field, type)) {
        return true;
      }
      if (type instanceof Class) {
        return ((Class<?>) type).isArray();
      }
      return false;
    }
  },

  ;


  private final String name;
  private final String description;
  private final Class<?>[] types;

  private FieldType(String name, String description, Class<?>[] clazz) {
    this.name = name;
    this.description = description;
    this.types = clazz;
  }


  public boolean isType(Field field, Type type) {
    for (Class<?> clazz : types) {
      if (type == clazz) {
        return true;
      }
    }
    return false;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Class<?>[] getTypes() {
    return types;
  }
}
