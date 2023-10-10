package com.benefitj.spring.quartz;

import com.benefitj.spring.quartz.worker.QuartzWorker;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuartzJobTaskImpl implements QuartzJobTask {

  String id;
  /**
   * group名称
   */
  String jobGroup;
  /**
   * Job名称
   */
  String jobName;
  /**
   * 任务别名
   */
  String jobAlias;
  /**
   * 描述
   */
  String description;
  /**
   * 是否不恢复
   */
  Boolean recovery;
  /**
   * 执行后是否持久化数据，默认不持久化
   */
  Boolean persistent;
  /**
   * 是否不允许并发执行，默认并发执行
   */
  Boolean disallowConcurrent;
  /**
   * Job的执行类型，参考: {@link JobType }
   */
  JobType jobType;
  /**
   * JobWorker的实现，或者被 {@link QuartzWorker} 注释的方法
   */
  String worker;
  /**
   * jobWorker的类型，参考：{@link WorkerType}
   */
  WorkerType workerType;
  /**
   * Job携带的数据
   */
  String jobData;
  /**
   * 触发器组
   */
  String triggerGroup;
  /**
   * 触发器名称
   */
  String triggerName;
  /**
   * 触发器的优先级
   */
  Integer priority;
  /**
   * 开始执行的时间
   */
  Long startAt;
  /**
   * 结束执行的时间
   */
  Long endAt;
  /**
   * Calendar
   */
  String calendarName;
  /**
   * 失效后的策略
   */
  Integer misfirePolicy;
  /**
   * 触发器类型: {@link TriggerType#SIMPLE}, {@link TriggerType#CRON}
   */
  TriggerType triggerType;
  /**
   * 每次执行的间隔
   */
  Long simpleInterval;
  /**
   * 重复次数
   */
  Integer simpleRepeatCount;
  /**
   * Cron表达式
   */
  String cronExpression;
  /**
   * 可用状态
   */
  Boolean active;
  /**
   * 机构ID
   */
  String orgId;
  /**
   * 拥有者
   */
  String ownerId;
  /**
   * 拥有者类型
   */
  String ownerType;
}
