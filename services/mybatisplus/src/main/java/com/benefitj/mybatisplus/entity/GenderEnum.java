package com.benefitj.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 性别
 */
@ApiModel("性别")
public enum GenderEnum implements IEnum<String> {
  @ApiModelProperty("男")
  MALE,
  @ApiModelProperty("女")
  FEMALE;

  @Override
  public String getValue() {
    return name().toLowerCase();
  }
}
