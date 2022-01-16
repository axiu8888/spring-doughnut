package com.benefitj.scaffold.quartz.mapper;

import com.benefitj.scaffold.base.SuperMapper;
import com.benefitj.scaffold.quartz.entity.SysJobTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Cron
 */
@Mapper
public interface QuartzJobTaskMapper extends SuperMapper<SysJobTaskEntity> {

  /**
   * 统计 job name 出现的次数
   *
   * @param jobName job name
   * @return 返回出现的次数
   */
  default long countJobName(@Param("jobName") String jobName) {
    return selectCount(lqw().eq(SysJobTaskEntity::getJobName, jobName));
  }

  /**
   * 统计 trigger name 出现的次数
   *
   * @param triggerName trigger name
   * @return 返回出现的次数
   */
  default long countTriggerName(@Param("triggerName") String triggerName) {
    return selectCount(lqw().eq(SysJobTaskEntity::getTriggerName, triggerName));
  }

}
