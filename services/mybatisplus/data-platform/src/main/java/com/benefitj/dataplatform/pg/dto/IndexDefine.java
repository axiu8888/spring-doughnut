package com.benefitj.dataplatform.pg.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 索引
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class IndexDefine {

  @JsonProperty("schemaname")
  private String schemaname;

  @JsonProperty("tablename")
  private String tablename;

  @JsonProperty("indexname")
  private String indexname;

  @JsonProperty("tablespace")
  private Object tablespace;

  /**
   * 索引的定义
   */
  @JsonProperty("indexdef")
  private String indexdef;

}
