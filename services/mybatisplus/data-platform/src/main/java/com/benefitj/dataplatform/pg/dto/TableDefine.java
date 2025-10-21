package com.benefitj.dataplatform.pg.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 表定义
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TableDefine extends DefineBase.Table {

  /**
   * 注释
   */
  @ApiModelProperty("表的注释")
  String tableComment;

  /**
   * 字段
   */
  @ApiModelProperty("字段定义")
  List<ColumnDefine> columns;

  /**
   * 主键
   */
  @ApiModelProperty("主键")
  List<String> primaryKeys;

  /**
   * 索引
   */
  @ApiModelProperty("索引")
  List<IndexDefine> indexes;

}
