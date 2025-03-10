package com.benefitj.dataplatform.pg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class ColumnInfo {

  @JsonProperty("table_catalog")
  private String tableCatalog;

  @JsonProperty("table_schema")
  private String tableSchema;

  @JsonProperty("table_name")
  private String tableName;

  @JsonProperty("column_name")
  private String columnName;

  @JsonProperty("ordinal_position")
  private Integer ordinalPosition;

  @JsonProperty("column_default")
  private Object columnDefault;

  @JsonProperty("is_nullable")
  private String isNullable;

  @JsonProperty("data_type")
  private String dataType;

  @JsonProperty("character_maximum_length")
  private Integer characterMaximumLength;

  @JsonProperty("character_octet_length")
  private Integer characterOctetLength;

  @JsonProperty("numeric_precision")
  private Object numericPrecision;

  @JsonProperty("numeric_precision_radix")
  private Object numericPrecisionRadix;

  @JsonProperty("numeric_scale")
  private Object numericScale;

  @JsonProperty("datetime_precision")
  private Object datetimePrecision;

  @JsonProperty("interval_type")
  private Object intervalType;

  @JsonProperty("interval_precision")
  private Object intervalPrecision;

  @JsonProperty("character_set_catalog")
  private Object characterSetCatalog;

  @JsonProperty("character_set_schema")
  private Object characterSetSchema;

  @JsonProperty("character_set_name")
  private Object characterSetName;

  @JsonProperty("collation_catalog")
  private Object collationCatalog;

  @JsonProperty("collation_schema")
  private Object collationSchema;

  @JsonProperty("collation_name")
  private Object collationName;

  @JsonProperty("domain_catalog")
  private Object domainCatalog;

  @JsonProperty("domain_schema")
  private Object domainSchema;

  @JsonProperty("domain_name")
  private Object domainName;

  @JsonProperty("udt_catalog")
  private String udtCatalog;

  @JsonProperty("udt_schema")
  private String udtSchema;

  @JsonProperty("udt_name")
  private String udtName;

  @JsonProperty("scope_catalog")
  private Object scopeCatalog;

  @JsonProperty("scope_schema")
  private Object scopeSchema;

  @JsonProperty("scope_name")
  private Object scopeName;

  @JsonProperty("maximum_cardinality")
  private Object maximumCardinality;

  @JsonProperty("dtd_identifier")
  private String dtdIdentifier;

  @JsonProperty("is_self_referencing")
  private String isSelfReferencing;

  @JsonProperty("is_identity")
  private String isIdentity;

  @JsonProperty("identity_generation")
  private Object identityGeneration;

  @JsonProperty("identity_start")
  private Object identityStart;

  @JsonProperty("identity_increment")
  private Object identityIncrement;

  @JsonProperty("identity_maximum")
  private Object identityMaximum;

  @JsonProperty("identity_minimum")
  private Object identityMinimum;

  @JsonProperty("identity_cycle")
  private String identityCycle;

  @JsonProperty("is_generated")
  private String isGenerated;

  @JsonProperty("generation_expression")
  private Object generationExpression;

  @JsonProperty("is_updatable")
  private String isUpdatable;

  @ApiModelProperty("注释")
  @JsonProperty("comment")
  private String comment;
}
