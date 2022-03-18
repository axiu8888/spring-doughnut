package com.benefitj.scaffold.mybatis;

import com.benefitj.core.ReflectUtils;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

public class FillValueHandler implements InterceptorHandler {

  @Override
  public Object intercept(Invocation invocation, MappedStatement statement, SqlCommandType type, Object parameter) throws Throwable {
    if (parameter != null) {
      if (type == SqlCommandType.INSERT) {
        handleInsert(invocation, statement, parameter, getFields(parameter.getClass(), f -> hasType(f, SqlCommandType.INSERT)));
      } else if (type == SqlCommandType.UPDATE) {
        handleUpdate(invocation, statement, parameter, getFields(parameter.getClass(), f -> hasType(f, SqlCommandType.UPDATE)));
      } else if (type == SqlCommandType.DELETE) {
        handleDelete(invocation, statement, parameter, getFields(parameter.getClass(), f -> hasType(f, SqlCommandType.DELETE)));
      }
    }
    return null;
  }

  public void handleInsert(Invocation invocation, MappedStatement statement, Object parameter, List<Field> fields) {
    for (Field field : fields) {
      Object value = ReflectUtils.getFieldValue(field, parameter);
      if (value != null && !(value instanceof CharSequence && StringUtils.isBlank(((CharSequence) value)))) {
        return;
      }
      switch (field.getName()) {
        case "createBy":
          ReflectUtils.setFieldValue(field, parameter, JwtTokenManager.currentUserId());
          break;
        case "createTime":
          ReflectUtils.setFieldValue(field, parameter, new Date());
          break;
      }
    }
  }

  public void handleUpdate(Invocation invocation, MappedStatement statement, Object parameter, List<Field> fields) {
    for (Field field : fields) {
      Object value = ReflectUtils.getFieldValue(field, parameter);
      if (value != null && !(value instanceof CharSequence && StringUtils.isBlank(((CharSequence) value)))) {
        return;
      }
      switch (field.getName()) {
        case "updateBy":
          ReflectUtils.setFieldValue(field, parameter, JwtTokenManager.currentUserId());
          break;
        case "updateTime":
          ReflectUtils.setFieldValue(field, parameter, new Date());
          break;
      }
    }
  }

  public void handleDelete(Invocation invocation, MappedStatement statement, Object parameter, List<Field> fields) {
  }

  private boolean hasType(Field f, SqlCommandType type) {
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


}
