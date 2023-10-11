package com.benefitj.mybatisplus.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benefitj.mybatisplus.entity.SysJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {

  /**
   * 统计 job name 出现的次数
   *
   * @param jobName job name
   * @return 返回出现的次数
   */
  default long countJobName(@Param("jobName") String jobName) {
    return selectCount(new QueryWrapper<SysJob>()
        .lambda()
        .eq(SysJob::getJobName, jobName)
    );
  }

  /**
   * 统计 trigger name 出现的次数
   *
   * @param triggerName trigger name
   * @return 返回出现的次数
   */
  default long countTriggerName(@Param("triggerName") String triggerName) {
    return selectCount(new QueryWrapper<SysJob>()
        .lambda()
        .eq(SysJob::getTriggerName, triggerName)
    );
  }

  /**
   * 查询调度任务的分页
   *
   * @param condition 条件
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @return 返回查询的列表
   */
  default List<SysJob> selectList(SysJob condition, Date startTime, Date endTime) {
    return selectList(new QueryWrapper<>(condition)
        .lambda()
        .ge(startTime != null, SysJob::getCreateTime, startTime)
        .ge(endTime != null, SysJob::getCreateTime, endTime)
        .ge(condition.getTriggerType() != null, SysJob::getTriggerType, condition.getTriggerType())
        .ge(condition.getJobGroup() != null, SysJob::getJobGroup, condition.getJobGroup())
        .ge(condition.getJobName() != null, SysJob::getJobName, condition.getJobName())
        .ge(condition.getTriggerGroup() != null, SysJob::getTriggerGroup, condition.getTriggerGroup())
        .ge(condition.getTriggerName() != null, SysJob::getTriggerName, condition.getTriggerName())
        .ge(condition.getOrgId() != null, SysJob::getOrgId, condition.getOrgId())
        .ge(condition.getOwnerId() != null, SysJob::getOwnerId, condition.getOwnerId())
        .ge(condition.getOwnerType() != null, SysJob::getOwnerType, condition.getOwnerType())
        .ge(condition.getActive() != null, SysJob::getActive, condition.getActive())
    );
  }
}
