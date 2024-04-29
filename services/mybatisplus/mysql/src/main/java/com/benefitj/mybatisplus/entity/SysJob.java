package com.benefitj.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.mybatisplus.entity.base.UuidEntity;
import com.benefitj.spring.quartz.JobType;
import com.benefitj.spring.quartz.QuartzJob;
import com.benefitj.spring.quartz.TriggerType;
import com.benefitj.spring.quartz.WorkerType;
import com.benefitj.spring.quartz.worker.QuartzWorker;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@ApiModel("quartz调度实体类")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_job")
@TableName("sys_job")
public class SysJob extends UuidEntity implements QuartzJob {
//  /**
//   * ID
//   */
//  @ApiModelProperty(value = "ID")
//  @TableId(type = IdType.ASSIGN_UUID)
//  @Id
//  @GeneratedValue
//  @Column(name = "id", columnDefinition = "varchar(32) comment '主键ID'", length = 32)
//  private String id;
  /**
   * group名称
   */
  @ApiModelProperty("group名称")
  @Column(name = "job_group", columnDefinition = "varchar(50) comment '任务分组'", length = 50)
  private String jobGroup;
  /**
   * Job名称
   */
  @ApiModelProperty("Job名称")
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
  @ApiModelProperty("描述")
  @Column(name = "description", columnDefinition = "varchar(1024) comment '任务描述'", length = 1024)
  private String description;
  /**
   * Job的执行类型，参考: {@link JobType }
   */
  @ApiModelProperty("Job的执行类型")
  @Column(name = "job_type", columnDefinition = "varchar(50) comment 'Job的执行类型'", length = 50)
  private JobType jobType;
  /**
   * JobWorker的实现，或者被 {@link QuartzWorker} 注释的方法
   */
  @ApiModelProperty("JobWorker的实现")
  @Column(name = "worker", columnDefinition = "varchar(50) comment 'JobWorker的实现类，或者被@QuartzWorker注释的方法'", length = 50)
  private String worker;
  /**
   * jobWorker的类型，参考：{@link WorkerType}
   */
  @ApiModelProperty("JobWorker的类型")
  @Column(name = "worker_type", columnDefinition = "varchar(50) comment 'JobWorker的类型'", length = 50)
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
   * 触发器类型: {@link TriggerType#SIMPLE}, {@link TriggerType#CRON}
   */
  @ApiModelProperty("触发器类型")
  @Column(name = "trigger_type", columnDefinition = "varchar(30) comment 'Cron表达式'", length = 30)
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
  @ApiModelProperty("重复次数")
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
  @Column(name = "active", columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 comment '可用状态'", length = 1)
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
  @ApiModelProperty("拥有者ID")
  @Column(name = "owner_id", columnDefinition = "varchar(32) comment '拥有者ID'", length = 32)
  private String ownerId;
  /**
   * 拥有者类型
   */
  @ApiModelProperty("拥有者类型")
  @Column(name = "owner_type", columnDefinition = "varchar(30) comment '拥有者类型'", length = 30)
  private String ownerType;

}