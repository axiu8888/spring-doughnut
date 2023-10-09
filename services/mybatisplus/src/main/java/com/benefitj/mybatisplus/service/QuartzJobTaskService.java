package com.benefitj.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.benefitj.core.IdUtils;
import com.benefitj.mybatisplus.dao.mapper.SysQuartzJobTaskMapper;
import com.benefitj.mybatisplus.entity.SysQuartzJobTask;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.quartz.IScheduler;
import com.benefitj.spring.quartz.QuartzException;
import com.benefitj.spring.quartz.QuartzUtils;
import com.benefitj.spring.quartz.TriggerType;
import com.benefitj.spring.security.jwt.token.JwtTokenManager;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class QuartzJobTaskService extends ServiceBase<SysQuartzJobTask, SysQuartzJobTaskMapper> {

  @Autowired
  IScheduler scheduler;

  public QuartzJobTaskService(SysQuartzJobTaskMapper mapper) {
    super(mapper);
  }

  /**
   * 获取 job name
   */
  private String nextJobName() {
    String name = null;
    for (int i = 0; i < 1000; i++) {
      name = IdUtils.nextId(8);
      if (countJobName(name) <= 0) {
        break;
      }
    }
    if (StringUtils.isBlank(name)) {
      throw new QuartzException("无法获取jobName");
    }
    return name;
  }

  /**
   * 统计 job name 出现的次数
   *
   * @param jobName job name
   * @return 返回出现的次数
   */
  public long countJobName(String jobName) {
    return getMapper().countJobName(jobName);
  }

  /**
   * 通过ID获取Cron的调度任务
   *
   * @param id 调度任务的ID
   * @return 返回调度任务
   */
  public SysQuartzJobTask get(String id) {
    return getMapper().selectOne(new QueryWrapper<SysQuartzJobTask>()
        .lambda()
        .eq(SysQuartzJobTask::getId, id)
    );
  }

  /**
   * 创建Cron调度任务
   *
   * @param task 调度任务
   */
  public SysQuartzJobTask create(SysQuartzJobTask task) {
    if (StringUtils.isBlank(task.getDescription())) {
      throw new QuartzException("请给此调度任务添加一个描述");
    }

    // 任务ID
    task.setId(IdUtils.uuid());
    // 机构ID
    task.setOrgId(StringUtils.getIfBlank(task.getOrgId(), JwtTokenManager::currentOrgId));
    // 创建时间
    task.setCreateTime(new Date());
    task.setActive(Boolean.TRUE);
    QuartzUtils.setup(task, nextJobName());
    QuartzUtils.scheduleJob(scheduler, task);
    // 保存
    super.save(task);
    return task;
  }

  /**
   * 更新调度任务
   *
   * @param task 调度任务
   * @return 返回
   */
  @Transactional(rollbackFor = Exception.class)
  public boolean update(SysQuartzJobTask task) {
    SysQuartzJobTask existTask = get(task.getId());
    if (existTask != null) {
      // 触发器组和名称、Job组合名称 都为自动生成，触发器类型必须指定

      TriggerType type = task.getTriggerType();
      if (type == null) {
        throw new QuartzException("请指定正确的触发器类型");
      }

      if (type != existTask.getTriggerType()) {
        throw new QuartzException("无法修改触发器类型");
      }

      SysQuartzJobTask copy = BeanHelper.copy(existTask, SysQuartzJobTask.class);
      BeanHelper.copy(task, existTask);
      existTask.setCreateTime(copy.getCreateTime());
      existTask.setUpdateTime(new Date());
      existTask.setActive(copy.getActive());

      // 重置调度
      QuartzUtils.setup(existTask, nextJobName());
      try {
        // 停止任务和触发器
        // 删除存在的job
        scheduler.pauseTrigger(QuartzUtils.triggerKey(copy));
        scheduler.deleteJob(QuartzUtils.jobKey(copy));
        // 重新调度
        QuartzUtils.scheduleJob(scheduler, existTask);
        return updateById(existTask);
      } catch (Exception e) {
        scheduler.resumeTrigger(QuartzUtils.triggerKey(existTask));
        throw new IllegalStateException(e);
      }
    }
    return false;
  }

  /**
   * 删除调度的任务
   *
   * @param id 任务的ID
   * @return 返回删除的条数(0或1)
   */
  @Transactional(rollbackFor = Exception.class)
  public int delete(String id) {
    SysQuartzJobTask task = get(id);
    if (task != null) {
      scheduler.pauseTrigger(QuartzUtils.triggerKey(task));
      scheduler.deleteJob(QuartzUtils.jobKey(task));
      return removeById(id) ? 1 : 0;
    }
    return 0;
  }

  /**
   * 改变 Job 的状态，暂停或执行
   *
   * @param id     任务ID
   * @param active 状态
   * @return 返回状态是否改变
   */
  @Transactional(rollbackFor = Exception.class)
  public boolean changeActive(String id, Boolean active) {
    final SysQuartzJobTask task = get(id);
    if (task != null) {
      task.setActive(Boolean.TRUE.equals(active));
      task.setUpdateTime(new Date());

      JobKey jobKey = QuartzUtils.jobKey(task);
      if (Boolean.TRUE.equals(active)) {
        QuartzUtils.scheduleJob(scheduler, task);
      } else {
        scheduler.pauseJob(jobKey);
      }
      return getMapper().updateById(task) > 0;
    }
    return false;
  }

  @Override
  public List<SysQuartzJobTask> getList(SysQuartzJobTask condition, Date startTime, Date endTime) {
    return getMapper().selectList(condition, startTime, endTime);
  }
}
