package com.benefitj.scaffold.quartz.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.scaffold.base.BaseEntity;
import com.benefitj.spring.quartz.JobType;
import com.benefitj.spring.quartz.QuartzJobTask;
import com.benefitj.spring.quartz.TriggerType;
import com.benefitj.spring.quartz.WorkerType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@ApiModel("Quartz调度任务")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableName("sys_job_task")
@Table(name = "sys_job_task", indexes = {
    @Index(name = "idx_org_user", columnList = "org_id, owner"),
    @Index(name = "idx_job_group", columnList = "job_group"),
    @Index(name = "idx_job_type", columnList = "job_type"),
    @Index(name = "idx_worker_type", columnList = "worker_type"),
})
public class SysJobTaskEntity extends BaseEntity implements QuartzJobTask {

  /**
   * 任务id
   */
  @ApiModelProperty("调度任务的ID")
  @Id
  @Column(name = "id", columnDefinition = "varchar(32) comment '调度任务的ID'", length = 32)
  private String id;
  /**
   * group名称
   */
  @ApiModelProperty("任务分组")
  @Column(name = "job_group", columnDefinition = "varchar(50) comment '任务分组'", length = 50)
  private String jobGroup;
  /**
   * Job名称
   */
  @ApiModelProperty("任务名称")
  @Column(name = "job_name", columnDefinition = "varchar(50) comment '任务名称'", length = 50)
  private String jobName;
  /**
   * 任务别名
   */
  @ApiModelProperty("任务别名")
  @Column(name = "job_alias", columnDefinition = "varchar(50) comment '任务别名'", length = 50)
  private String jobAlias;
  /**
   * 描述
   */
  @ApiModelProperty("任务描述")
  @Column(name = "description", columnDefinition = "varchar(1024) comment '任务描述'", length = 1024)
  private String description;
  /**
   * 是否异步
   */
  @ApiModelProperty("是否异步执行程序")
  @Column(name = "async", columnDefinition = "tinyint(1) comment '是否异步执行程序' DEFAULT 0", length = 1)
  private Boolean async;
  /**
   * 是否恢复
   */
  @ApiModelProperty("是否恢复")
  @Column(name = "recovery", columnDefinition = "tinyint(1) comment '是否恢复' DEFAULT 0", length = 1)
  private Boolean recovery;
  /**
   * 执行后是否持久化数据，默认不持久化
   */
  @ApiModelProperty("执行后是否持久化数据")
  @Column(name = "persistent", columnDefinition = "tinyint(1) comment '执行后是否持久化数据，默认不持久化' DEFAULT 0", length = 1)
  private Boolean persistent;
  /**
   * 是否不允许并发执行，默认并发执行
   */
  @ApiModelProperty("是否不允许并发执行")
  @Column(name = "disallow_concurrent", columnDefinition = "tinyint(1) comment '是否不允许并发执行，默认并发执行' DEFAULT 1", length = 1)
  private Boolean disallowConcurrent;
  /**
   * Job的执行类型，参考: {@link JobType }
   */
  @ApiModelProperty("Job的执行类型")
  @Column(name = "job_type", columnDefinition = "varchar(50) comment 'Job的执行类型'", length = 50)
  private JobType jobType;
  /**
   * JobWorker的实现
   */
  @ApiModelProperty("JobWorker的实现类")
  @Column(name = "worker", columnDefinition = "varchar(50) comment 'JobWorker的实现类'", length = 50)
  private String worker;
  /**
   * jobWorker的类型，参考：{@link WorkerType}
   */
  @ApiModelProperty("jobWorker的类型")
  @Column(name = "worker_type", columnDefinition = "varchar(50) comment 'jobWorker的类型'", length = 50)
  private WorkerType workerType;
  /**
   * Job携带的数据
   */
  @ApiModelProperty("Job携带的数据")
  @Column(name = "job_data", columnDefinition = "varchar(1024) comment 'Job携带的数据'", length = 1024)
  private String jobData;
  /**
   * 触发器组
   */
  @ApiModelProperty("触发器组")
  @Column(name = "trigger_group", columnDefinition = "varchar(50) comment '触发器组'", length = 50)
  private String triggerGroup;
  /**
   * 触发器名称
   */
  @ApiModelProperty("触发器名称")
  @Column(name = "trigger_name", columnDefinition = "varchar(50) comment '触发器名称'", length = 50)
  private String triggerName;
  /**
   * 触发器的优先级
   */
  @ApiModelProperty("触发器的优先级")
  @Column(name = "priority", columnDefinition = "integer comment '触发器的优先级' DEFAULT 50")
  private Integer priority;
  /**
   * 开始执行的时间
   */
  @ApiModelProperty("开始执行的时间")
  @Column(name = "start_at", columnDefinition = "bigint comment '开始执行的时间'")
  private Long startAt;
  /**
   * 结束执行的时间
   */
  @ApiModelProperty("结束执行的时间")
  @Column(name = "end_at", columnDefinition = "bigint comment '结束执行的时间'")
  private Long endAt;
  /**
   * Calendar
   */
  @ApiModelProperty("Calendar Name")
  @Column(name = "calendar_name", columnDefinition = "varchar(50) comment 'Calendar Name'", length = 50)
  private String calendarName;
  /**
   * 失效后的策略
   */
  @ApiModelProperty("失效后的策略")
  @Column(name = "misfire_policy", columnDefinition = "integer comment '失效后的策略'")
  private Integer misfirePolicy;
  /**
   * 触发器类型: {@link TriggerType#SIMPLE}, {@link TriggerType#CRON}
   */
  @ApiModelProperty("拥有者类型")
  @Column(name = "trigger_type", columnDefinition = "varchar(30) comment '触发器类型: SIMPLE/CRON'", length = 30)
  private TriggerType triggerType;
  /**
   * 每次执行的间隔
   */
  @ApiModelProperty("每次执行的间隔")
  @Column(name = "simple_interval", columnDefinition = "bigint comment '每次执行的间隔'")
  private Long simpleInterval;
  /**
   * 重复次数
   */
  @ApiModelProperty("Simple任务的重复次数")
  @Column(name = "simple_repeat_count", columnDefinition = "integer comment '重复次数'")
  private Integer simpleRepeatCount;
  /**
   * Cron表达式
   */
  @ApiModelProperty("Cron表达式")
  @Column(name = "cron_expression", columnDefinition = "varchar(50) comment 'Cron表达式'", length = 50)
  private String cronExpression;
  /**
   * 可用状态
   */
  @ApiModelProperty("可用状态")
  @Column(name = "active", columnDefinition = "tinyint(1) NOT NULL DEFAULT 1 comment '可用状态'", length = 1)
  private Boolean active;
  /**
   * 机构ID
   */
  @ApiModelProperty("机构ID")
  @Column(name = "org_id", columnDefinition = "varchar(32) comment '机构ID'", length = 32)
  private String orgId;
  /**
   * 拥有者
   */
  @ApiModelProperty("拥有者")
  @Column(name = "owner", columnDefinition = "varchar(32) comment '拥有者'", length = 32)
  private String owner;
  /**
   * 拥有者类型
   */
  @ApiModelProperty("拥有者类型")
  @Column(name = "owner_type", columnDefinition = "varchar(30) comment '拥有者类型'", length = 30)
  private String ownerType;

}
