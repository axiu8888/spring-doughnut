package com.benefitj.spring.mvc.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 分页请求
 *
 * @param <T>
 */
@ApiModel("分页请求")
public class PageRequest<T> extends QueryRequest<T> {

  /**
   * 页码，默认第一页
   */
  @ApiModelProperty("页码，默认第一页")
  private Integer pageNum = 1;
  /**
   * 分页大小，默认10条
   */
  @ApiModelProperty("分页大小，默认10条")
  private Integer pageSize = 10;
  /**
   * 是否为多层级
   */
  @ApiModelProperty("是否为多层级")
  private Boolean multiLevel = null;
  /**
   * active是否起作用
   */
  @ApiModelProperty("active是否起作用")
  private Boolean active = null;

  public PageRequest() {
  }

  /**
   * @param pageNum  must not be less than zero.
   * @param pageSize must not be less than one.
   */
  public PageRequest(Integer pageNum, Integer pageSize) {
    this.setPageNum(pageNum);
    this.setPageSize(pageSize);
  }

  public PageRequest(Integer pageNum, Integer pageSize, List<String> orderBy) {
    this(pageNum, pageSize);
    this.setOrderBy(orderBy);
  }

  public PageRequest(Integer pageNum, Integer pageSize, List<String> orderBy, T condition) {
    this(pageNum, pageSize, orderBy);
    this.setCondition(condition);
  }

  public Integer getPageNum() {
    return pageNum;
  }

  public void setPageNum(Integer pageNum) {
    this.pageNum = (pageNum != null) ? Math.max(pageNum, 0) : 1;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = (pageSize != null) ? Math.max(pageSize, 1) : 10;
  }

  public Boolean getMultiLevel() {
    return multiLevel;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }


  public long getOffset() {
    return (long) getPageNum() * (long) getPageSize();
  }

  public boolean hasPrevious() {
    return pageNum > 0;
  }

  public PageRequest<T> previousOrFirst() {
    return hasPrevious() ? previous() : first();
  }

  public PageRequest<T> next() {
    return new PageRequest<>(getPageNum() + 1, getPageSize(), getOrderBy());
  }

  public PageRequest<T> previous() {
    return getPageNum() == 0 ? this : new PageRequest<>(getPageNum() - 1, getPageSize(), getOrderBy());
  }

  public PageRequest<T> first() {
    return new PageRequest<>(0, getPageSize(), getOrderBy());
  }

  /**
   * 是否获取多层级数据
   */
  public boolean isMultiLevel() {
    return Boolean.TRUE.equals(getMultiLevel());
  }

  public void setMultiLevel(Boolean multiLevel) {
    this.multiLevel = multiLevel;
  }

  public static final class Builder<T> {
    private T condition;
    private Date startTime;
    private Date endTime;
    private List<String> orderBy;

    private Integer pageNum;
    private Integer pageSize;
    private Boolean multiLevel;
    private Boolean active;

    public Builder() {
    }

    public Builder(PageRequest<T> copy) {
      this.condition = copy.getCondition();
      this.startTime = copy.getStartTime();
      this.endTime = copy.getEndTime();
      this.orderBy = copy.getOrderBy();

      this.pageNum = copy.getPageNum();
      this.pageSize = copy.getPageSize();
      this.multiLevel = copy.getMultiLevel();
      this.active = copy.getActive();
    }

    public Builder<T> setPageNum(Integer pageNum) {
      this.pageNum = pageNum;
      return this;
    }

    public Builder<T> setPageSize(Integer pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public Builder<T> setOrderBy(List<String> orderBy) {
      this.orderBy = orderBy;
      return this;
    }

    public Builder<T> setMultiLevel(Boolean multiLevel) {
      this.multiLevel = multiLevel;
      return this;
    }

    public Builder<T> setActive(Boolean active) {
      this.active = active;
      return this;
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

    public PageRequest<T> build() {
      PageRequest<T> request = new PageRequest<>();
      request.setCondition(this.condition);
      request.setStartTime(this.startTime);
      request.setEndTime(this.endTime);
      request.setOrderBy(this.orderBy);
      request.setPageNum(this.pageNum);
      request.setPageSize(this.pageSize);
      request.setMultiLevel(this.multiLevel);
      request.setActive(this.active);
      return request;
    }
  }
}
