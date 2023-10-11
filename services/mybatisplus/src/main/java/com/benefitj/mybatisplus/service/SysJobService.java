package com.benefitj.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.benefitj.mybatisplus.dao.mapper.SysJobMapper;
import com.benefitj.mybatisplus.entity.SysJob;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.quartz.IScheduler;
import com.benefitj.spring.quartz.QuartzException;
import com.benefitj.spring.quartz.QuartzUtils;
import com.benefitj.spring.quartz.TriggerType;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SysJobService extends ServiceBase<SysJob, SysJobMapper> {

  @Autowired
  IScheduler scheduler;

  public SysJobService(SysJobMapper mapper) {
    super(mapper);
  }

  public IScheduler getScheduler() {
    return scheduler;
  }

  /**
   * 通过ID获取Cron的调度任务
   *
   * @param id 调度任务的ID
   * @return 返回调度任务
   */
  public SysJob get(String id) {
    return getMapper().selectOne(new QueryWrapper<SysJob>()
        .lambda()
        .eq(SysJob::getId, id)
    );
  }

  /**
   * 创建Cron调度任务
   *
   * @param job 调度任务
   */
  public SysJob create(SysJob job) {
    if (StringUtils.isBlank(job.getDescription())) {
      throw new QuartzException("请给此调度任务添加一个描述");
    }
    job.setActive(Boolean.TRUE);
    QuartzUtils.scheduleJob(scheduler, job);
    // 保存
    super.save(job);
    return job;
  }

  /**
   * 更新调度任务
   *
   * @param job 调度任务
   * @return 返回
   */
  @Transactional(rollbackFor = Exception.class)
  public boolean update(SysJob job) {
    SysJob existJob = get(job.getId());
    if (existJob != null) {
      // 触发器组和名称、Job组合名称 都为自动生成，触发器类型必须指定

      TriggerType type = job.getTriggerType();
      if (type == null) {
        throw new QuartzException("请指定正确的触发器类型");
      }

      if (type != existJob.getTriggerType()) {
        throw new QuartzException("无法修改触发器类型");
      }

      SysJob copy = BeanHelper.copy(existJob, SysJob.class);
      BeanHelper.copy(job, existJob);
      existJob.setCreateTime(copy.getCreateTime());
      existJob.setUpdateTime(new Date());
      existJob.setActive(copy.getActive());

      // 重置调度
      QuartzUtils.setup(existJob);
      try {
        // 停止任务和触发器
        // 删除存在的job
        scheduler.pauseTrigger(QuartzUtils.triggerKey(copy));
        scheduler.deleteJob(QuartzUtils.jobKey(copy));
        // 重新调度
        QuartzUtils.scheduleJob(scheduler, existJob);
        return updateById(existJob);
      } catch (Exception e) {
        scheduler.resumeTrigger(QuartzUtils.triggerKey(existJob));
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
    SysJob job = get(id);
    if (job != null) {
      scheduler.pauseTrigger(QuartzUtils.triggerKey(job));
      scheduler.deleteJob(QuartzUtils.jobKey(job));
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
    final SysJob job = get(id);
    if (job != null) {
      job.setActive(Boolean.TRUE.equals(active));
      job.setUpdateTime(new Date());

      JobKey jobKey = QuartzUtils.jobKey(job);
      if (Boolean.TRUE.equals(active)) {
        QuartzUtils.scheduleJob(scheduler, job);
      } else {
        scheduler.pauseJob(jobKey);
      }
      return getMapper().updateById(job) > 0;
    }
    return false;
  }

  @Override
  public List<SysJob> getList(SysJob condition, Date startTime, Date endTime) {
    return getMapper().selectList(condition, startTime, endTime);
  }

}
