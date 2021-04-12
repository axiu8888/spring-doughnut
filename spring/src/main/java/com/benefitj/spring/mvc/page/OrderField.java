package com.benefitj.spring.mvc.page;

import org.apache.commons.lang3.StringUtils;

public class OrderField {

  /**
   * 属性
   */
  private String property;
  /**
   * 排序规则
   */
  private OrderDirection direction;
  /**
   * 字段
   */
  private String column;

  public OrderField() {
  }

  public OrderField(String property, OrderDirection direction) {
    this.property = property;
    this.direction = direction;
  }

  public OrderField(String property, String direction) {
    this(property, OrderDirection.fromOptionalString(direction).orElse(null));
  }

  public OrderField(String property, OrderDirection direction, String column) {
    this(property, direction);
    this.column = column;
  }

  public String getProperty() {
    return property;
  }

  public void setProperty(String property) {
    this.property = property;
  }

  public OrderDirection getDirection() {
    return direction;
  }

  public void setDirection(OrderDirection direction) {
    this.direction = direction;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public boolean isAscending() {
    return direction != null && direction.isAscending();
  }

  public boolean isDescending() {
    return direction != null && direction.isDescending();
  }

  @Override
  public String toString() {
    OrderDirection direction = getDirection();
    String column = StringUtils.isNotBlank(getColumn()) ? getColumn() : getProperty();
    return direction == null ? column : column + " " + direction.name();
  }
}
