package com.benefitj.dataplatform.pg.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * 表定义基类
 */
public interface DefineBase {

  /**
   * 表基本信息
   */
  @SuperBuilder
  @NoArgsConstructor
  @Data
  public class Table {

    /**
     * 数据库名
     */
    @JSONField(name = "table_catalog")
    @JsonProperty("table_catalog")
    private String tableCatalog;

    /**
     * 模式名（Schema）	public
     */
    @JSONField(name = "table_schema")
    @JsonProperty("table_schema")
    private String tableSchema;

    /**
     * 表名
     */
    @JSONField(name = "table_name")
    @JsonProperty("table_name")
    private String tableName;

    /**
     * 表类型: {@link TableType}
     */
    @JSONField(name = "table_type")
    @JsonProperty("table_type")
    private String tableType;

  }



}
