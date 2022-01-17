package com.benefitj.system.controller.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@ApiModel("API模块")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiModule {


  @ApiModelProperty("上下文路径")
  private String contextPath;

  @ApiModelProperty("类名")
  private String className;

  /**
   * API的标签 {@link io.swagger.annotations.Api#tags()}
   */
  @ApiModelProperty("API的标签")
  private List<String> apiTags;

  @ApiModelProperty("Controller上的路径")
  private List<String> baseUrls;

  @ApiModelProperty("API详情")
  private List<ApiDetail> apiDetails;

}
