package com.benefitj.scaffold.mybatis;

import com.benefitj.scaffold.security.token.JwtTokenManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * 填充值处理
 */
public class FillValueHandler implements InterceptorHandler {

  @Override
  public Object intercept(Invocation invocation, MappedStatement statement, SqlCommandType type, Object parameter) throws Throwable {
    if (parameter != null) {
      if (type == SqlCommandType.INSERT) {
        handleInsert(invocation, statement, parameter, getFields(parameter, SqlCommandType.INSERT));
      } else if (type == SqlCommandType.UPDATE) {
        handleUpdate(invocation, statement, parameter, getFields(parameter, SqlCommandType.UPDATE));
      } else if (type == SqlCommandType.DELETE) {
        handleDelete(invocation, statement, parameter, getFields(parameter, SqlCommandType.DELETE));
      }
    }
    return null;
  }

  public void handleInsert(Invocation invocation, MappedStatement statement, Object parameter, List<Field> fields) {
    for (Field field : fields) {
      Object value = getFieldValue(field, parameter);
      if (value != null && !(value instanceof CharSequence && StringUtils.isBlank(((CharSequence) value)))) {
        return;
      }
      switch (field.getName()) {
        case "createBy":
          setFieldValue(field, parameter, JwtTokenManager.currentUserId());
          break;
        case "createTime":
          setFieldValue(field, parameter, new Date());
          break;
      }
    }
  }

  public void handleUpdate(Invocation invocation, MappedStatement statement, Object parameter, List<Field> fields) {
    for (Field field : fields) {
      Object value = getFieldValue(field, parameter);
      if (value != null && !(value instanceof CharSequence && StringUtils.isBlank(((CharSequence) value)))) {
        return;
      }
      switch (field.getName()) {
        case "updateBy":
          setFieldValue(field, parameter, JwtTokenManager.currentUserId());
          break;
        case "updateTime":
          setFieldValue(field, parameter, new Date());
          break;
      }
    }
  }

  public void handleDelete(Invocation invocation, MappedStatement statement, Object parameter, List<Field> fields) {
  }

  protected boolean hasType(Field f, SqlCommandType type) {
    if (f.isAnnotationPresent(FillValue.class)) {
      FillValue fv = f.getAnnotation(FillValue.class);
      for (SqlCommandType sct : fv.types()) {
        if (sct == type) {
          return true;
        }
      }
    }
    return false;
  }

  protected List<Field> getFields(Object parameter, SqlCommandType type) {
    return getFields(parameter.getClass(), f -> hasType(f, type));
  }

}
