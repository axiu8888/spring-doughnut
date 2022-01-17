package com.benefitj.system.controller.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 资源
 */
@ApiModel("资源")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiResourceItem {

  @ApiModelProperty("上下文路径")
  private String contextPath;

  /**
   * API的标签 {@link io.swagger.annotations.Api#tags()}
   */
  @ApiModelProperty("API的标签")
  private String[] apiTags;

  @ApiModelProperty("API路径")
  private String path;

  @ApiModelProperty("HTTP方法")
  private String[] httpMethods;

}
