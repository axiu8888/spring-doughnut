package com.benefitj.mybatisplus.mybatis;

import org.apache.ibatis.mapping.MappedStatement;

/**
 * 字段值填充器
 */
public interface FieldValueFiller {

  /**
   * 是否支持的类型
   *
   * @param target 目标对象
   * @param ms     SQL信息
   * @return 返回是否支持
   */
  boolean support(Object target, MappedStatement ms);

  /**
   * 填充字段值
   *
   * @param target 目标对象
   * @param ms     SQL信息
   */
  void fill(Object target, MappedStatement ms);

}
