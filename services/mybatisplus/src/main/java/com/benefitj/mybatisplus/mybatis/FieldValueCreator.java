package com.benefitj.mybatisplus.mybatis;

public interface FieldValueCreator<T> {

  /**
   * 创建值
   *
   * @param target 目标对象
   * @return 返回创建的值
   */
  T create(Object target);

}
