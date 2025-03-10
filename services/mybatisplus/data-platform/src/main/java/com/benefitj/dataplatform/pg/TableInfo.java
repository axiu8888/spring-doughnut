package com.benefitj.dataplatform.pg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TableInfo {

  /**
   * 表名
   */
  String name;

  /**
   * 注释
   */
  String comment;

  /**
   * 字段
   */
  List<ColumnInfo> columns;

}
