package com.benefitj.mybatisplus.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benefitj.mybatisplus.entity.SysQuartzJobTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface SysQuartzJobTaskMapper extends BaseMapper<SysQuartzJobTask> {

  /**
   * 统计 job name 出现的次数
   *
   * @param jobName job name
   * @return 返回出现的次数
   */
  default long countJobName(@Param("jobName") String jobName) {
    return selectCount(new QueryWrapper<SysQuartzJobTask>()
        .lambda()
        .eq(SysQuartzJobTask::getJobName, jobName)
    );
  }

  /**
   * 统计 trigger name 出现的次数
   *
   * @param triggerName trigger name
   * @return 返回出现的次数
   */
  default long countTriggerName(@Param("triggerName") String triggerName) {
    return selectCount(new QueryWrapper<SysQuartzJobTask>()
        .lambda()
        .eq(SysQuartzJobTask::getTriggerName, triggerName)
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
  default List<SysQuartzJobTask> selectList(SysQuartzJobTask condition, Date startTime, Date endTime) {
    return selectList(new QueryWrapper<SysQuartzJobTask>()
        .lambda()
        .ge(startTime != null, SysQuartzJobTask::getCreateTime, startTime)
        .ge(endTime != null, SysQuartzJobTask::getCreateTime, endTime)
        .ge(condition.getTriggerType() != null, SysQuartzJobTask::getTriggerType, condition.getTriggerType())
        .ge(condition.getJobGroup() != null, SysQuartzJobTask::getJobGroup, condition.getJobGroup())
        .ge(condition.getJobName() != null, SysQuartzJobTask::getJobName, condition.getJobName())
        .ge(condition.getTriggerGroup() != null, SysQuartzJobTask::getTriggerGroup, condition.getTriggerGroup())
        .ge(condition.getTriggerName() != null, SysQuartzJobTask::getTriggerName, condition.getTriggerName())
        .ge(condition.getOrgId() != null, SysQuartzJobTask::getOrgId, condition.getOrgId())
        .ge(condition.getOwner() != null, SysQuartzJobTask::getOwner, condition.getOwner())
        .ge(condition.getOwnerType() != null, SysQuartzJobTask::getOwnerType, condition.getOwnerType())
        .ge(condition.getActive() != null, SysQuartzJobTask::getActive, condition.getActive())
    );
  }
}
