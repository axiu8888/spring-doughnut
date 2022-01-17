package com.benefitj.system.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@ApiModel("API接口")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiDetail {

  @ApiModelProperty("类名")
  private String className;

  @ApiModelProperty("方法名")
  private String methodName;

  @ApiModelProperty("API操作描述")
  private String apiOperation;

  @ApiModelProperty("API路径")
  private List<String> paths;

  @ApiModelProperty("HTTP方法")
  private List<String> methods;

  @ApiModelProperty("Controller上的路径")
  private List<String> baseUrls;

}
