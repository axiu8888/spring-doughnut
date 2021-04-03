package com.benefitj.spring.quartz;

import java.util.Date;

public interface QuartzJobTask {

  int TRIGGER_PRIORITY = 50;

  /**
   * 获取调度的ID
   */
  String getId();

  /**
   * 调度唯一ID
   *
   * @param id ID
   */
  void setId(String id);

  /**
   * 获取 Job 组
   */
  String getJobGroup();

  /**
   * 设置 Job 组
   *
   * @param jobGroup Job 组
   */
  void setJobGroup(String jobGroup);

  /**
   * 获取 Job 名称
   */
  String getJobName();

  /**
   * 设置 Job 名称
   *
   * @param jobName Job 名称
   */
  void setJobName(String jobName);

  /**
   * 获取 Job 别名
   */
  String getJobAlias();

  /**
   * 获取 Job 别名
   *
   * @param jobAlias 别名
   */
  void setJobAlias(String jobAlias);

  /**
   * 获取调度的描述
   */
  String getDescription();

  /**
   * 设置对此调度的描述
   *
   * @param description 调度的描述
   */
  void setDescription(String description);

  /**
   * 获取是否异步执行
   */
  Boolean getAsync();

  /**
   * 设置是否异步执行
   *
   * @param async 是否异步
   */
  void setAsync(Boolean async);

  /**
   * 获取是否恢复
   */
  Boolean getRecovery();

  /**
   * 设置是否恢复
   *
   * @param recovery 是否恢复
   */
  void setRecovery(Boolean recovery);

  /**
   * 获取是否持久化
   */
  Boolean getPersistent();

  /**
   * 设置是否持久化
   *
   * @param persistent 是否持久化
   */
  void setPersistent(Boolean persistent);

  /**
   * 获取是否允许并发执行
   */
  Boolean getDisallowConcurrent();

  /**
   * 设置是否允许并发执行
   *
   * @param disallowConcurrent 是否允许并发
   */
  void setDisallowConcurrent(Boolean disallowConcurrent);

  /**
   * 获取 Job 类型
   */
  String getJobType();

  /**
   * 设置 Job 类型
   *
   * @param jobType Job 类型
   */
  void setJobType(String jobType);

  /**
   * 获取 JobWorker 的实现
   */
  String getWorker();

  /**
   * 设置 JobWorker 的实现
   *
   * @param worker 实现类/组件名称
   */
  void setWorker(String worker);

  /**
   * 获取Worker类型, 参考 {@link WorkerType}
   */
  String getWorkerType();

  /**
   * 设置 Worker 类型, 参考 {@link WorkerType}
   *
   * @param workerType  Worker 类型
   */
  void setWorkerType(String workerType);

  /**
   * 获取Job数据
   */
  String getJobData();

  /**
   * 设置Job数据
   *
   * @param jobData 数据
   */
  void setJobData(String jobData);

  /**
   * 获取触发器组
   */
  String getTriggerGroup();

  /**
   * 设置触发器组
   *
   * @param triggerGroup 触发器组
   */
  void setTriggerGroup(String triggerGroup);

  /**
   * 获取触发器名称
   */
  String getTriggerName();

  /**
   * 设置触发器名称
   *
   * @param triggerName 触发器名称
   */
  void setTriggerName(String triggerName);

  /**
   * 获取调度优先级
   */
  Integer getPriority();

  /**
   * 设置调度优先级
   *
   * @param priority 优先级
   */
  void setPriority(Integer priority);

  /**
   * 获取开始时间
   */
  Long getStartAt();

  /**
   * 设置开始时间
   *
   * @param startAt 时间
   */
  void setStartAt(Long startAt);

  /**
   * 获取结束时间
   */
  Long getEndAt();

  /**
   * 设置结束时间
   *
   * @param endAt 时间
   */
  void setEndAt(Long endAt);

  /**
   * 获取日历名称
   */
  String getCalendarName();

  /**
   * 设置日历名称
   *
   * @param calendarName 名称
   */
  void setCalendarName(String calendarName);

  /**
   * 获取失效策略
   */
  Integer getMisfirePolicy();

  /**
   * 设置失效策略
   *
   * @param misfirePolicy 策略
   */
  void setMisfirePolicy(Integer misfirePolicy);

  /**
   * 获取调度器类型
   */
  String getTriggerType();

  /**
   * 设置触发器类型
   *
   * @param triggerType 触发器类型(SIMPLE/CRON)
   */
  void setTriggerType(String triggerType);

  /**
   * 获取简单调度的时间间隔
   */
  Long getSimpleInterval();

  /**
   * 设置简单调度的间隔时间（毫秒）
   *
   * @param simpleInterval 时间
   */
  void setSimpleInterval(Long simpleInterval);

  /**
   * 获取简单调度的次数
   */
  Integer getSimpleRepeatCount();

  /**
   * 设置简单调度的次数
   *
   * @param simpleRepeatCount 次数
   */
  void setSimpleRepeatCount(Integer simpleRepeatCount);

  /**
   * 获取CRON表达式
   */
  String getCronExpression();

  /**
   * 设置CRON表达式
   *
   * @param cronExpression 表达式
   */
  void setCronExpression(String cronExpression);

  /**
   * 获取是否可用
   */
  Boolean getActive();

  /**
   * 是否可用
   *
   * @param active 状态
   */
  void setActive(Boolean active);

  /**
   * 获取创建时间
   */
  Date getCreateTime();

  /**
   * 设置创建时间
   *
   * @param createTime 时间
   */
  void setCreateTime(Date createTime);

  /**
   * 获取更新时间
   */
  Date getUpdateTime();

  /**
   * 设置更新时间
   *
   * @param updateTime 时间
   */
  void setUpdateTime(Date updateTime);

}
