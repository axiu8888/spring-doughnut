package com.benefitj.mybatisplus.dao.mybatis;

import java.lang.reflect.Field;

public interface FieldValueCreator {

  /**
   * 创建值
   *
   * @param target 目标对象
   * @param field  字段
   * @return 返回创建的值
   */
  Object create(Object target, Field field);

}
