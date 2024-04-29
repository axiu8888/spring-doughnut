package com.benefitj.mybatisplus.service.entitydescriptor;

import com.benefitj.core.ReflectUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * 字段描述
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PropertyDescriptor {
  /**
   * 字段
   */
  private String name;
  /**
   * 列
   */
  private String column;
  /**
   * 类型
   */
  private Class<?> type;
  /**
   * 字段
   */
  private Field field;
  /**
   * 是否为主键
   */
  private boolean primaryKey;

  public void setFieldValue(Object obj, Object value) {
    ReflectUtils.setFieldValue(getField(), obj, value);
  }
}
