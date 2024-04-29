package com.benefitj.mybatisplus.dao.mybatis;

import com.benefitj.core.ReflectUtils;

import java.lang.reflect.Field;

public interface NullValueFieldValueCreator extends FieldValueCreator {

  @Override
  default Object create(Object target, Field field) {
    Object value = ReflectUtils.getFieldValue(field, target);
    return value != null ? value : createDefault(target);
  }

  Object createDefault(Object target);

}
