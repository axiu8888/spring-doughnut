package com.benefitj.spring.quartz;

import com.benefitj.spring.quartz.worker.QuartzWorker;
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
   * {@link QuartzWorker} 注解
   */
  @ApiModelProperty("被@QuartzWorker注释的方法")
  QUARTZ_WORKER,

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
