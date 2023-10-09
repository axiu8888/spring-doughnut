package com.benefitj.mybatisplus.dao.mybatis;

import com.benefitj.core.ReflectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 简单的字段填充器
 */
public class SimpleFieldValueFiller implements FieldValueFiller {

  private Map<String, FieldValueCreator> fields;
  private List<SqlCommandType> sqlCommandTypes = Arrays.asList(SqlCommandType.values());

  public SimpleFieldValueFiller(Map<String, FieldValueCreator> fields) {
    this.fields = fields;
  }

  public SimpleFieldValueFiller(Map<String, FieldValueCreator> fields,
                                List<SqlCommandType> sqlCommandTypes) {
    this(fields);
    this.sqlCommandTypes = sqlCommandTypes;
  }

  @Override
  public boolean support(Object target, MappedStatement ms) {
    SqlCommandType sqt = ms.getSqlCommandType();
    for (SqlCommandType type : getSqlCommandTypes()) {
      if (sqt == type) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void fill(Object target, MappedStatement ms) {
    getFields().forEach((name, creator) -> setFieldValue(target, name, creator.create(target)));
  }

  public Object setFieldValue(Object target, String name, Object value) {
    Field field = ReflectUtils.getField(target.getClass(), name);
    if (field != null) {
      Object rawValue = ReflectUtils.getFieldValue(field, target);
      if (rawValue == null) {
        ReflectUtils.setFieldValue(field, target, value);
        return value;
      } else if (rawValue instanceof CharSequence && StringUtils.isBlank((CharSequence) rawValue)) {
        ReflectUtils.setFieldValue(field, target, value);
        return value;
      }
      return rawValue;
    }
    return null;
  }


  public Map<String, FieldValueCreator> getFields() {
    return fields;
  }

  public void setFields(Map<String, FieldValueCreator> fields) {
    this.fields = fields;
  }

  public List<SqlCommandType> getSqlCommandTypes() {
    return sqlCommandTypes;
  }

  public void setSqlCommandTypes(List<SqlCommandType> sqlCommandTypes) {
    this.sqlCommandTypes = sqlCommandTypes;
  }
}
