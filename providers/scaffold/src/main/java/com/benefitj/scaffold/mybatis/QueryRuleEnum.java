package com.benefitj.scaffold.mybatis;


import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * Query 规则 常量
 */
public enum QueryRuleEnum {

  GT(">", "gt", "大于"),
  GE(">=", "ge", "大于等于"),
  LT("<", "lt", "小于"),
  LE("<=", "le", "小于等于"),
  EQ("=", "eq", "等于"),
  NE("!=", "ne", "不等于"),
  IN("IN", "in", "包含"),
  LIKE("LIKE", "like", "全模糊"),
  LEFT_LIKE("LEFT_LIKE", "left_like", "左模糊"),
  RIGHT_LIKE("RIGHT_LIKE", "right_like", "右模糊"),
  EQ_WITH_ADD("EQWITHADD", "eq_with_add", "带加号等于"),
  LIKE_WITH_AND("LIKEWITHAND", "like_with_and", "多词模糊匹配————暂时未用上"),
  SQL_RULES("USE_SQL_RULES", "ext", "自定义SQL片段");


  @ApiModelProperty("运算符")
  private String operator;

  @ApiModelProperty("函数")
  private String func;

  @ApiModelProperty("文本描述")
  private String remarks;

  QueryRuleEnum(String operator, String func, String remarks) {
    this.operator = operator;
    this.func = func;
    this.remarks = remarks;
  }

  public static QueryRuleEnum getByValue(String value) {
    if (StringUtils.isBlank(value)) {
      return null;
    }
    for (QueryRuleEnum val : values()) {
      if (val.getOperator().equals(value) || val.getFunc().equals(value)) {
        return val;
      }
    }
    return null;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public String getFunc() {
    return func;
  }

  public void setFunc(String func) {
    this.func = func;
  }

}
