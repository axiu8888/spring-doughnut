package com.benefitj.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benefitj.mybatisplus.entity.base.EntityBase;
import com.benefitj.mybatisplus.service.entitydescriptor.EntityDescriptor;
import com.benefitj.mybatisplus.service.entitydescriptor.EntityFinder;
import com.benefitj.mybatisplus.service.entitydescriptor.PropertyDescriptor;
import com.benefitj.spring.mvc.query.OrderUtils;
import com.benefitj.spring.mvc.query.PageRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 抽象的基类
 *
 * @param <T> 实体类
 * @param <M> Mapper
 */
public abstract class ServiceBase<T extends EntityBase, M extends BaseMapper<T>> extends ServiceImpl<M, T> implements IService<T> {

  protected EntityDescriptor getEntityDescriptor() {
    return EntityFinder.INSTANCE.getEntityClass(getClass(), "T");
  }

  /**
   * 创建查询包装器
   */
  protected QueryWrapper<T> newQueryWrapper(@Nullable T condition) {
    return new QueryWrapper<>(condition);
  }

  /**
   * 统计
   *
   * @param condition 条件
   * @return 返回统计的条数
   */
  public long count(T condition) {
    return getBaseMapper().selectCount(new QueryWrapper<>(condition));
  }

  /**
   * 统计
   *
   * @param ids ID
   * @return 返回统计的条数
   */
  public long countByPK(List<?> ids) {
    return getBaseMapper().selectCount(new QueryWrapper<T>().in(getEntityDescriptor().getId().getColumn(), ids));
  }

  /**
   * 获取列表
   *
   * @param condition 条件
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @return 返回查询的列表
   */
  public List<T> getList(T condition, @Nullable Date startTime, @Nullable Date endTime) {
    return getBaseMapper().selectList(newQueryWrapper(condition)
        .lambda()
        .ge(startTime != null, EntityBase::getCreateTime, startTime)
        .le(endTime != null, EntityBase::getCreateTime, endTime)
    );
  }

  /**
   * 获取分页
   *
   * @param request 分页参数
   * @return 返回分页信息
   */
  public PageInfo<T> getPage(PageRequest<T> request) {
    // ORDER BY
    String orderBy = String.join(",", getOrderByList(request.getOrderBy()));
    Date startTime = request.getStartTime();
    Date endTime = request.getEndTime();
    // 分页
    return PageHelper.startPage(request.getPageNum(), request.getPageSize(), orderBy).doSelectPageInfo(()
        -> getList(request.getCondition(), startTime, endTime/*, request.isMultiLevel()*/));
  }

  /**
   * 获取真正的排序字段
   *
   * @param orders 排序列表
   * @return 返回排序字段
   */
  protected List<String> getOrderByList(List<String> orders) {
    if (orders != null && !orders.isEmpty()) {
      EntityDescriptor entityDescriptor = getEntityDescriptor();
      return OrderUtils.convert(orders)
          .stream()
          .map(order -> {
            PropertyDescriptor descriptor = entityDescriptor.get(order.getProperty());
            if (descriptor != null) {
              String column = descriptor.getColumn();
              order.setColumn(column);
              return order.toString();
            }
            return null;
          })
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

}
