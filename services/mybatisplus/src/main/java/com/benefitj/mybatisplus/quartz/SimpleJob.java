package com.benefitj.mybatisplus.quartz;


import com.benefitj.mybatisplus.entity.SysJob;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.quartz.QuartzJob;
import com.benefitj.spring.quartz.TriggerType;
import com.benefitj.spring.quartz.WorkerType;
import com.benefitj.spring.quartz.worker.QuartzWorker;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class SimpleJob {

  public static SimpleJob from(QuartzJob job) {
    SimpleJob copy = BeanHelper.copy(job, SimpleJob.class);
    copy.setTriggerType(TriggerType.SIMPLE);
    return copy;
  }

  public static SysJob to(SimpleJob job) {
    SysJob copy = BeanHelper.copy(job, SysJob.class);
    copy.setTriggerType(TriggerType.SIMPLE);
    return copy;
  }


  /**
   * ID
   */
  @ApiModelProperty(value = "ID")
  private String id;
  /**
   * 任务别名
   */
  @ApiModelProperty("任务别名")
  private String jobAlias;
  /**
   * 描述
   */
  @ApiModelProperty("描述")
  private String description;
  /**
   * 是否不恢复
   */
  @ApiModelProperty("是否不恢复")
  private Boolean recovery;
  /**
   * JobWorker的实现，或者被 {@link QuartzWorker} 注释的方法
   */
  @ApiModelProperty("JobWorker的实现")
  private String worker;
  /**
   * jobWorker的类型，参考：{@link WorkerType}
   */
  @ApiModelProperty("JobWorker的类型")
  private WorkerType workerType;
  /**
   * Job携带的数据
   */
  @ApiModelProperty("Job携带的数据")
  private String jobData;
  /**
   * 开始执行的时间
   */
  @ApiModelProperty("开始执行的时间")
  private Long startAt;
  /**
   * 结束执行的时间
   */
  @ApiModelProperty("结束执行的时间")
  private Long endAt;
  /**
   * 触发器类型: {@link TriggerType#SIMPLE}, {@link TriggerType#CRON}
   */
  @ApiModelProperty("触发器类型")
  @Builder.Default
  private TriggerType triggerType = TriggerType.SIMPLE;
  /**
   * 每次执行的间隔
   */
  @ApiModelProperty("每次执行的间隔")
  private Long simpleInterval;
  /**
   * 重复次数
   */
  @ApiModelProperty("重复次数")
  @Builder.Default
  private Integer simpleRepeatCount = 0;
  /**
   * 可用状态
   */
  @ApiModelProperty("可用状态")
  private Boolean active;
  /**
   * 机构ID
   */
  @ApiModelProperty("机构ID")
  private String orgId;
  /**
   * 拥有者
   */
  @ApiModelProperty("拥有者ID")
  private String ownerId;
  /**
   * 拥有者类型
   */
  @ApiModelProperty("拥有者类型")
  private String ownerType;

  public SysJob toJob() {
    return to(this);
  }
}
