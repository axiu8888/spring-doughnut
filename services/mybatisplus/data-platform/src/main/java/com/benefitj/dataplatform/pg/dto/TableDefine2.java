package com.benefitj.dataplatform.pg.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TableDefine2 extends DefineBase.Table {

  /**
   *
   */
  @JSONField(name = "self_referencing_column_name")
  @JsonProperty("self_referencing_column_name")
  private Object selfReferencingColumnName;

  /**
   *
   */
  @JSONField(name = "reference_generation")
  @JsonProperty("reference_generation")
  private Object referenceGeneration;

  /**
   *
   */
  @JSONField(name = "user_defined_type_catalog")
  @JsonProperty("user_defined_type_catalog")
  private Object userDefinedTypeCatalog;

  /**
   *
   */
  @JSONField(name = "user_defined_type_schema")
  @JsonProperty("user_defined_type_schema")
  private Object userDefinedTypeSchema;

  /**
   *
   */
  @JSONField(name = "user_defined_type_name")
  @JsonProperty("user_defined_type_name")
  private Object userDefinedTypeName;

  /**
   * 是否可插入
   * YES / NO
   */
  @JSONField(name = "is_insertable_into")
  @JsonProperty("is_insertable_into")
  private String isInsertableInto;

  /**
   * YES / NO
   */
  @JSONField(name = "is_typed")
  @JsonProperty("is_typed")
  private String isTyped;

  /**
   *
   */
  @JSONField(name = "commit_action")
  @JsonProperty("commit_action")
  private Object commitAction;

}
