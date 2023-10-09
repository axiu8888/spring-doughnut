package com.benefitj.spring.quartz;

import com.benefitj.spring.quartz.job.QuartzJob;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * worker 类型
 */
@ApiModel("worker 类型")
public enum WorkerType {
  /**
   * 新建对象
   */
  @ApiModelProperty("根据class创建新对象")
  NEW_INSTANCE,
  /**
   * spring 组件名
   */
  @ApiModelProperty("spring 组件名")
  SPRING_BEAN_NAME,
  /**
   * spring 组件类
   */
  @ApiModelProperty("spring 组件类")
  SPRING_BEAN_CLASS,
  /**
   * {@link QuartzJob} 注解
   */
  @ApiModelProperty("被@QuartzJob注释的方法")
  QUARTZ_JOB_WORKER,

  ;

  /**
   * 获取 WorkerType
   */
  public static WorkerType of(String type) {
    if (type != null && !type.isEmpty()) {
      for (WorkerType wt : values()) {
        if (wt.name().equalsIgnoreCase(type)) {
          return wt;
        }
      }
    }
    return null;
  }
}
