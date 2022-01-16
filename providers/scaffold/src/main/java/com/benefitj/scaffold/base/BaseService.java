package com.benefitj.scaffold.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.benefitj.scaffold.base.entitydescriptor.EntityDescriptor;
import com.benefitj.scaffold.base.entitydescriptor.EntityFinder;
import com.benefitj.scaffold.base.entitydescriptor.PropertyDescriptor;
import com.benefitj.spring.mvc.page.OrderUtils;
import com.benefitj.spring.mvc.page.PageableRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import javax.annotation.Nullable;
import java.io.Serializable;
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
public abstract class BaseService<M extends SuperMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements IService<T> {

  /**
   * 创建查询包装器
   */
  protected QueryWrapper<T> qw(@Nullable T condition) {
    QueryWrapper<T> qw = new QueryWrapper<>(condition);
    getEntityDescriptor()
        .get(pd -> pd.getDeclaringClass() != BaseEntity.class)
        .forEach(pd -> {
          Object v = pd.getFieldValue(condition);
          qw.eq(v != null, pd.getColumn(), v);
        });
    return qw;
  }

  /**
   * 统计
   *
   * @param condition 条件
   * @return 返回统计的条数
   */
  public long count(T condition) {
    return getBaseMapper().selectCount(qw(condition));
  }

  /**
   * 统计
   *
   * @param id ID
   * @return 返回统计的条数
   */
  public long countById(Serializable id) {
    return countById(List.of(id));
  }

  /**
   * 统计
   *
   * @param ids ID
   * @return 返回统计的条数
   */
  public long countById(List<?> ids) {
    return getBaseMapper().selectCount(qw(null).in(getEntityDescriptor().getId().getColumn(), ids));
  }

  /**
   * 通过ID删除
   *
   * @param id 主键
   * @return 删除的条数
   */
  public int deleteById(Serializable id) {
    return getBaseMapper().deleteById(id);
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
    return getBaseMapper().selectList(qw(condition)
        .lambda()
        .ge(startTime != null, BaseEntity::getCreateTime, startTime)
        .le(endTime != null, BaseEntity::getCreateTime, endTime)
    );
  }

  /**
   * 获取分页
   *
   * @param request 分页参数
   * @return 返回分页信息
   */
  public PageInfo<T> getPage(PageableRequest<T> request) {
    // ORDER BY
    String orderBy = String.join(",", getOrderByList(request.getOrderBy()));
    Date startTime = request.getStartTime();
    Date endTime = request.getEndTime();
    // 分页
    return PageHelper.startPage(request.getPageNum(), request.getPageSize(), orderBy)
        .doSelectPageInfo(() -> getList(request.getCondition(), startTime, endTime));
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

  protected EntityDescriptor getEntityDescriptor() {
    return EntityFinder.INSTANCE.find(getClass(), "T");
  }

}
