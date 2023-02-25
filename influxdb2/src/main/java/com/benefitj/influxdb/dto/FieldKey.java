package com.benefitj.influxdb.dto;

/**
 * 字段
 */
public class FieldKey {
  /**
   * 字段名
   */
  private String column;
  /**
   * 是否为TAG
   */
  private boolean tag = false;
  /**
   * 值的下标
   */
  private int index = -1;
  /**
   * 是否为时间戳
   */
  private boolean timestamp = false;
  /**
   * field class type
   */
  private Class<?> fieldType;

  public FieldKey() {
  }

  public FieldKey(String column, boolean tag, int index) {
    this.column = column;
    this.tag = tag;
    this.index = index;
    this.timestamp = "time".equals(getColumn());
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

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public boolean isTimestamp() {
    return timestamp;
  }

  public void setTimestamp(boolean timestamp) {
    this.timestamp = timestamp;
  }

  public Class<?> getFieldType() {
    return fieldType;
  }

  public void setFieldType(Class<?> fieldType) {
    this.fieldType = fieldType;
  }

  public void setFieldType(String fieldType) {
    this.fieldType = getFieldType(fieldType);
  }

  public Number getNumber(Object value) {
    return getNumber(value, (Class<Number>) getFieldType());
  }

  public Number getNumber(Object value, Class<? extends Number> type) {
    if (!(value instanceof Number)) {
      throw new IllegalArgumentException("required number value");
    }
    Number tmpValue;
    if(type == Long.class) {
      tmpValue = ((Number)value).longValue();
    } else if (type == Double.class) {
      tmpValue = ((Number)value).doubleValue();
    } else if (type == Float.class) {
      tmpValue = ((Number)value).floatValue();
    } else if (type == Integer.class) {
      tmpValue = ((Number)value).doubleValue();
    } else if (type == Short.class) {
      tmpValue = ((Number)value).shortValue();
    } else if (type == Byte.class) {
      tmpValue = ((Number)value).byteValue();
    } else {
      tmpValue = (Number) value;
    }
    return tmpValue;
  }


  public static Class<?> getFieldType(String type) {
    switch (type) {
      case "integer":
        return Long.class;
      case "float":
        return Double.class;
      case "string":
        return String.class;
      case "boolean":
      case "bool":
        return Boolean.class;
      default:
        throw new IllegalArgumentException("not support");
    }
  }

  public static final class Builder {
    private String column;
    private boolean tag;
    private int index;
    private boolean timestamp;
    private Class<?> fieldType;

    public Builder() {
    }

    public Builder setColumn(String column) {
      this.column = column;
      return this;
    }

    public Builder setTag(boolean tag) {
      this.tag = tag;
      return this;
    }

    public Builder setIndex(int index) {
      this.index = index;
      return this;
    }

    public Builder setTimestamp(boolean timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder setFieldType(Class<?> fieldType) {
      this.fieldType = fieldType;
      return this;
    }

    public FieldKey build() {
      FieldKey fieldKey = new FieldKey();
      fieldKey.setColumn(column);
      fieldKey.setTag(tag);
      fieldKey.setIndex(index);
      fieldKey.setTimestamp(timestamp);
      fieldKey.setFieldType(fieldType);
      return fieldKey;
    }
  }
}
