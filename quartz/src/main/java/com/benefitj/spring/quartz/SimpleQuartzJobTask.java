package com.benefitj.spring.quartz;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class SimpleQuartzJobTask implements QuartzJobTask {

  /**
   * 调度任务的ID
   */
  private String id;
  /**
   * group名称, 任务分组
   */
  private String jobGroup;
  /**
   * Job名称, 任务名称
   */
  private String jobName;
  /**
   * 任务别名
   */
  private String jobAlias;
  /**
   * 任务描述
   */
  private String description;
  /**
   * 是否异步执行程序
   */
  private Boolean async;
  /**
   * 是否不恢复
   */
  private Boolean recovery;
  /**
   * 执行后是否持久化数据，默认不持久化
   */
  private Boolean persistent;
  /**
   * 是否不允许并发执行，默认并发执行
   */
  private Boolean disallowConcurrent;
  /**
   * Job的执行类型，参考: {@link JobType }
   */
  private String jobType;
  /**
   * JobWorker的实现
   */
  private String worker;
  /**
   * jobWorker的类型，参考：{@link WorkerType}
   */
  private String workerType;
  /**
   * Job携带的数据
   */
  private String jobData;
  /**
   * 触发器组
   */
  private String triggerGroup;
  /**
   * 触发器名称
   */
  private String triggerName;
  /**
   * 触发器的优先级
   */
  private Integer priority;
  /**
   * 开始执行的时间
   */
  private Long startAt;
  /**
   * 结束执行的时间
   */
  private Long endAt;
  /**
   * Calendar
   */
  private String calendarName;
  /**
   * 失效后的策略
   */
  private Integer misfirePolicy;
  /**
   * 触发器类型: {@link TriggerType#SIMPLE}, {@link TriggerType#CRON}
   */
  private String triggerType;
  /**
   * 每次执行的间隔
   */
  private Long simpleInterval;
  /**
   * 重复次数
   */
  private Integer simpleRepeatCount;
  /**
   * Cron表达式
   */
  private String cronExpression;
  /**
   * 可用状态
   */
  private Boolean active;
  /**
   * 创建时间
   */
  @JSONField(serialize = false)
  @JsonIgnore
  private Date createTime;
  /**
   * 更新时间
   */
  @JSONField(serialize = false)
  @JsonIgnore
  private Date updateTime;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getJobGroup() {
    return jobGroup;
  }

  @Override
  public void setJobGroup(String jobGroup) {
    this.jobGroup = jobGroup;
  }

  @Override
  public String getJobName() {
    return jobName;
  }

  @Override
  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  @Override
  public String getJobAlias() {
    return jobAlias;
  }

  @Override
  public void setJobAlias(String jobAlias) {
    this.jobAlias = jobAlias;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public Boolean getAsync() {
    return async;
  }

  @Override
  public void setAsync(Boolean async) {
    this.async = async;
  }

  @Override
  public Boolean getRecovery() {
    return recovery;
  }

  @Override
  public void setRecovery(Boolean recovery) {
    this.recovery = recovery;
  }

  @Override
  public Boolean getPersistent() {
    return persistent;
  }

  @Override
  public void setPersistent(Boolean persistent) {
    this.persistent = persistent;
  }

  @Override
  public Boolean getDisallowConcurrent() {
    return disallowConcurrent;
  }

  @Override
  public void setDisallowConcurrent(Boolean disallowConcurrent) {
    this.disallowConcurrent = disallowConcurrent;
  }

  @Override
  public String getJobType() {
    return jobType;
  }

  @Override
  public void setJobType(String jobType) {
    this.jobType = jobType;
  }

  @Override
  public String getWorker() {
    return worker;
  }

  @Override
  public void setWorker(String worker) {
    this.worker = worker;
  }

  @Override
  public String getWorkerType() {
    return workerType;
  }

  @Override
  public void setWorkerType(String workerType) {
    this.workerType = workerType;
  }

  @Override
  public String getJobData() {
    return jobData;
  }

  @Override
  public void setJobData(String jobData) {
    this.jobData = jobData;
  }

  @Override
  public String getTriggerGroup() {
    return triggerGroup;
  }

  @Override
  public void setTriggerGroup(String triggerGroup) {
    this.triggerGroup = triggerGroup;
  }

  @Override
  public String getTriggerName() {
    return triggerName;
  }

  @Override
  public void setTriggerName(String triggerName) {
    this.triggerName = triggerName;
  }

  @Override
  public Integer getPriority() {
    return priority;
  }

  @Override
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  @Override
  public Long getStartAt() {
    return startAt;
  }

  @Override
  public void setStartAt(Long startAt) {
    this.startAt = startAt;
  }

  @Override
  public Long getEndAt() {
    return endAt;
  }

  @Override
  public void setEndAt(Long endAt) {
    this.endAt = endAt;
  }

  @Override
  public String getCalendarName() {
    return calendarName;
  }

  @Override
  public void setCalendarName(String calendarName) {
    this.calendarName = calendarName;
  }

  @Override
  public Integer getMisfirePolicy() {
    return misfirePolicy;
  }

  @Override
  public void setMisfirePolicy(Integer misfirePolicy) {
    this.misfirePolicy = misfirePolicy;
  }

  @Override
  public String getTriggerType() {
    return triggerType;
  }

  @Override
  public void setTriggerType(String triggerType) {
    this.triggerType = triggerType;
  }

  @Override
  public Long getSimpleInterval() {
    return simpleInterval;
  }

  @Override
  public void setSimpleInterval(Long simpleInterval) {
    this.simpleInterval = simpleInterval;
  }

  @Override
  public Integer getSimpleRepeatCount() {
    return simpleRepeatCount;
  }

  @Override
  public void setSimpleRepeatCount(Integer simpleRepeatCount) {
    this.simpleRepeatCount = simpleRepeatCount;
  }

  @Override
  public String getCronExpression() {
    return cronExpression;
  }

  @Override
  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  @Override
  public Boolean getActive() {
    return active;
  }

  @Override
  public void setActive(Boolean active) {
    this.active = active;
  }

  @Override
  public Date getCreateTime() {
    return createTime;
  }

  @Override
  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  @Override
  public Date getUpdateTime() {
    return updateTime;
  }

  @Override
  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }
}
