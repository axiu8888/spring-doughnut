package com.benefitj.spring.influxdb.convert;

import com.benefitj.core.ReflectUtils;

import java.lang.reflect.Field;

/**
 * 字段
 */
public class ColumnField {

  /**
   * field
   */
  private final Field field;
  /**
   * measurement name
   */
  private String measurement;
  /**
   * 字段名
   */
  private String column;
  /**
   * 是否为tag
   */
  private boolean tag = false;
  /**
   * tag是否允许为null
   */
  private boolean tagNullable = false;

  public ColumnField(Field field) {
    this.field = field;
  }

  public Field getField() {
    return field;
  }

  public String getMeasurement() {
    return measurement;
  }

  public void setMeasurement(String measurement) {
    this.measurement = measurement;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public boolean isTag() {
    return tag;
  }

  public void setTag(boolean tag) {
    this.tag = tag;
  }

  public boolean isTagNullable() {
    return tagNullable;
  }

  public void setTagNullable(boolean tagNullable) {
    this.tagNullable = tagNullable;
  }

  /**
   * 获取对象的值
   *
   * @param o 对象
   * @return 返回对象字段的值
   */
  public Object getValue(Object o) {
    return ReflectUtils.getFieldValue(getField(), o);
  }

}
