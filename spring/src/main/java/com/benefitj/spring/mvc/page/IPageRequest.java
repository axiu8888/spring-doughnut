package com.benefitj.spring.mvc.page;

import java.util.Date;

/**
 * 分页请求
 *
 * @param <T>
 */
public interface IPageRequest<T> {

  /**
   * 获取页码
   */
  Integer getPageNum();

  /**
   * 设置页码
   *
   * @param pageNum 页码
   */
  void setPageNum(Integer pageNum);

  /**
   * 获取分页大小
   */
  Integer getPageSize();

  /**
   * 设置分页大小
   *
   * @param pageSize 分页大小
   */
  void setPageSize(Integer pageSize);

  /**
   * 获取查询条件
   */
  T getCondition();

  /**
   * 设置查询条件
   *
   * @param condition 条件
   */
  void setCondition(T condition);

  /**
   * 获取开始时间
   */
  Date getStartTime();

  /**
   * 设置开始时间
   *
   * @param startTime 开始时间
   */
  void setStartTime(Date startTime);

  /**
   * 获取结束时间
   */
  Date getEndTime();

  /**
   * 设置结束时间
   *
   * @param endTime 结束时间
   */
  void setEndTime(Date endTime);

}
