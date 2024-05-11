package com.benefitj.mybatisplus.entity.postgresql;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("业务类型")
public enum BusinessType {

  @ApiModelProperty("填写")
  filein,
  @ApiModelProperty("填空+执行")
  filein_exec,
  @ApiModelProperty("填空+上传报告")
  filein_upload,
  @ApiModelProperty("执行类")
  exec,
  @ApiModelProperty("上传类")
  upload,
  @ApiModelProperty("问卷：question")
  question,
  @ApiModelProperty("配置类问卷")
  quesAnswer,
  @ApiModelProperty("填空+执行+上传报告")
  filein_exec_upload,
  @ApiModelProperty("直接得报告，由处方或者结果直接产生，目前使用场景为营养膳食和康复评定")
  directReport,

}
