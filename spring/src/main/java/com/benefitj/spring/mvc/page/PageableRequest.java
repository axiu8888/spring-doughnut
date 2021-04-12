package com.benefitj.spring.mvc.page;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 请求分页
 *
 * @param <T>
 */
public class PageableRequest<T> {

  public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

  /**
   * 页码，默认第一页
   */
  private Integer pageNum = 1;
  /**
   * 分页大小，默认10条
   */
  private Integer pageSize = 10;
  /**
   * 排序
   */
  private List<String> orderBy = Collections.emptyList();
  /**
   * 是否为多层级
   */
  private Boolean multiLevel = null;
  /**
   * active是否起作用
   */
  private Boolean active = null;
  /**
   * 条件
   */
  private T condition;
  /**
   * 开始时间
   */
  @JsonFormat(pattern = DATE_PATTERN)
  @DateTimeFormat(pattern = DATE_PATTERN)
  private Date startTime;
  /**
   * 结束时间
   */
  @JsonFormat(pattern = DATE_PATTERN)
  @DateTimeFormat(pattern = DATE_PATTERN)
  private Date endTime;

  public PageableRequest() {
  }

  /**
   * @param pageNum  must not be less than zero.
   * @param pageSize must not be less than one.
   */
  public PageableRequest(Integer pageNum, Integer pageSize) {
    this.setPageNum(pageNum);
    this.setPageSize(pageSize);
  }

  public PageableRequest(Integer pageNum, Integer pageSize, List<String> orderBy) {
    this(pageNum, pageSize);
    this.setOrderBy(orderBy);
  }

  public PageableRequest(Integer pageNum, Integer pageSize, List<String> orderBy, T condition) {
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

  public List<String> getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(List<String> orderBy) {
    this.orderBy = orderBy != null ? orderBy : Collections.emptyList();
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

  public long getOffset() {
    return (long) getPageNum() * (long) getPageSize();
  }

  public boolean hasPrevious() {
    return pageNum > 0;
  }

  public PageableRequest<T> previousOrFirst() {
    return hasPrevious() ? previous() : first();
  }

  public PageableRequest<T> next() {
    return new PageableRequest<>(getPageNum() + 1, getPageSize(), getOrderBy());
  }

  public PageableRequest<T> previous() {
    return getPageNum() == 0 ? this : new PageableRequest<>(getPageNum() - 1, getPageSize(), getOrderBy());
  }

  public PageableRequest<T> first() {
    return new PageableRequest<>(0, getPageSize(), getOrderBy());
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
    private Integer pageNum;
    private Integer pageSize;
    private List<String> orderBy;
    private Boolean multiLevel;
    private Boolean active;
    private T condition;
    private Date startTime;
    private Date endTime;

    public Builder() {
    }

    public Builder(PageableRequest<T> copy) {
      this.pageNum = copy.getPageNum();
      this.pageSize = copy.getPageSize();
      this.orderBy = copy.getOrderBy();
      this.multiLevel = copy.getMultiLevel();
      this.active = copy.getActive();
      this.condition = copy.getCondition();
      this.startTime = copy.getStartTime();
      this.endTime = copy.getEndTime();
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

    public PageableRequest<T> build() {
      PageableRequest<T> page = new PageableRequest<>();
      page.setPageNum(this.pageNum);
      page.setPageSize(this.pageSize);
      page.setOrderBy(this.orderBy);
      page.setMultiLevel(this.multiLevel);
      page.setActive(this.active);
      page.setCondition(this.condition);
      page.setStartTime(this.startTime);
      page.setEndTime(this.endTime);
      return page;
    }
  }
}
