package com.benefitj.scaffold.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.benefitj.scaffold.base.entitydescriptor.EntityDescriptor;
import com.benefitj.scaffold.base.entitydescriptor.EntityFinder;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

public interface SuperMapper<T extends BaseEntity> extends BaseMapper<T> {

  /**
   * 查询调度任务的分页
   *
   * @param condition 条件
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @return 返回查询的列表
   */
  @Transient
  default List<T> selectList(T condition, Date startTime, Date endTime) {
    return selectList(qw(condition)
        .orderBy(false, false, LambdaUtils.extract(BaseEntity::getCreateTime).getImplMethodName())
        .lambda()
        .ge(startTime != null, BaseEntity::getCreateTime, startTime)
        .le(endTime != null, BaseEntity::getCreateTime, endTime)
        .eq(condition.getVersion() != null, BaseEntity::getVersion, condition.getVersion())
        .eq(condition.getActive() != null, BaseEntity::getActive, condition.getActive())
    );
  }

  /**
   * 获取实体类型
   */
  @Transient
  default EntityDescriptor getEntityDescriptor() {
    return EntityFinder.INSTANCE.find(getClass(), "T");
  }

  /**
   * 获取实体类型
   */
  @Transient
  default Class<T> getEntityClass() {
    return (Class<T>) getEntityDescriptor().getEntityType();
  }

  /**
   * 查询条件包装
   *
   * @param condition 条件
   * @return 返回查询包装器
   */
  @Transient
  default QueryWrapper<T> qw(T condition) {
    QueryWrapper<T> wrapper = new QueryWrapper<>(condition);
    getEntityDescriptor()
        .get(pd -> pd.getField().getDeclaringClass() != BaseEntity.class)
        .forEach(pd -> {
          Object v = pd.getFieldValue(condition);
          wrapper.eq(v != null, pd.getColumn(), v);
        });
    return wrapper;
  }

  /**
   * 查询条件包装
   *
   * @return 返回查询包装器
   */
  @Transient
  default LambdaQueryWrapper<T> lqw() {
    return new LambdaQueryWrapper<T>();
  }

}
