package com.hsrg.fileserver.controller.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@ApiModel("文件")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileItemVo {

  @ApiModelProperty("文件名")
  private String name;

  @ApiModelProperty("大小")
  private long size;

  @ApiModelProperty("后缀")
  private String suffix;

  @ApiModelProperty("内容类型")
  private String contentType;

  @ApiModelProperty("修改时间")
  private long lastModified;

  @ApiModelProperty("文件的地址")
  private String url;

  @ApiModelProperty("元数据")
  private Map<String, String> metadata;

}
