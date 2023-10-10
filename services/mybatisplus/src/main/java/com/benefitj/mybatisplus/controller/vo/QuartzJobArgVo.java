package com.benefitj.mybatisplus.controller.vo;

import com.benefitj.spring.quartz.worker.ArgType;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("参数")
@SuperBuilder
@NoArgsConstructor
@Data
public class QuartzJobArgVo {

  /**
   * 参数的位置
   */
  private int position;
  /**
   * 参数名称
   */
  private String name;
  /**
   * 参数类型
   */
  private ArgType type;
  /**
   * 描述
   */
  private String description;

}
