package com.hsrg.fileserver.controller.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@ApiModel("上传结果")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class UploadVo extends FileItemVo {

  @ApiModelProperty("上传结果")
  private int code;
  @ApiModelProperty("上传结果提示")
  private String message;
}
