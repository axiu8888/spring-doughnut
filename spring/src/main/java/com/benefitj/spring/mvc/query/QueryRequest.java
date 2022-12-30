package com.benefitj.spring.mvc.query;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@ApiModel("查询请求")
public class QueryRequest<T> {

  public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

  /**
   * 条件
   */
  @ApiModelProperty("条件")
  private T condition;
  /**
   * 开始时间
   */
  @ApiModelProperty(value = "开始时间: " + DATE_PATTERN, dataType = "String")
  @JsonFormat(pattern = DATE_PATTERN)
  @DateTimeFormat(pattern = DATE_PATTERN)
  private Date startTime;
  /**
   * 结束时间
   */
  @ApiModelProperty(value = "结束时间: " + DATE_PATTERN, dataType = "String")
  @JsonFormat(pattern = DATE_PATTERN)
  @DateTimeFormat(pattern = DATE_PATTERN)
  private Date endTime;
  /**
   * 排序
   */
  @ApiModelProperty("排序")
  private List<String> orderBy = Collections.emptyList();

  /**
   * 全部参数
   */
  @JSONField(serialize = false, deserialize = false)
  @JsonIgnore
  private JSONObject parameters;

  public QueryRequest() {
  }

  public QueryRequest(Date startTime, Date endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public QueryRequest(T condition, Date startTime, Date endTime) {
    this.condition = condition;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public T getCondition() {
    return condition;
  }

  public void setCondition(T condition) {
    this.condition = condition;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public List<String> getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(List<String> orderBy) {
    this.orderBy = orderBy != null ? orderBy : Collections.emptyList();
  }

  public JSONObject getParameters() {
    return parameters;
  }

  public void setParameters(JSONObject parameters) {
    this.parameters = parameters;
  }

  public static final class Builder<T> {
    private T condition;
    private Date startTime;
    private Date endTime;
    private List<String> orderBy;

    public Builder() {
    }

    public Builder(PageRequest<T> copy) {
      this.condition = copy.getCondition();
      this.startTime = copy.getStartTime();
      this.endTime = copy.getEndTime();
      this.orderBy = copy.getOrderBy();
    }

    public Builder<T> setCondition(T condition) {
      this.condition = condition;
      return this;
    }

    public Builder<T> setStartTime(Date startTime) {
      this.startTime = startTime;
      return this;
    }

    public Builder<T> setEndTime(Date endTime) {
      this.endTime = endTime;
      return this;
    }

    public Builder<T> setOrderBy(List<String> orderBy) {
      this.orderBy = orderBy;
      return this;
    }

    public QueryRequest<T> build() {
      QueryRequest<T> request = new QueryRequest<>();
      request.setCondition(this.condition);
      request.setStartTime(this.startTime);
      request.setEndTime(this.endTime);
      request.setOrderBy(this.orderBy);
      return request;
    }
  }
}
