package com.benefitj.dataplatform.pg.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * 字段名                          解释                            示例                     备注 <br/>
 * table_catalog                  数据库名                        support                  表所在的数据库名 <br/>
 * table_schema                   模式名（Schema）                public                   表所在的模式（通常为public） <br/>
 * table_name                     表名                            test                    表的名称 <br/>
 * column_name                    列名                            id                      列的名称 <br/>
 * primary_key                    是否为主键                    true 或 false               如果为主键，则为 true <br/>
 * ordinal_position               列在表中的位置（序号）            1                        从 1 开始的索引 <br/>
 * column_default                 列的默认值                    CURRENT_TIMESTAMP          如果列有默认值，显示默认值 <br/>
 * is_nullable                    列是否允许为 NULL                YES 或 NO                是否允许为空 <br/>
 * data_type                      列的数据类型                    integer                   数据类型（如 integer, varchar） <br/>
 * character_maximum_length       字符最大长度                    32                        对于字符串类型（如 varchar），最大长度 <br/>
 * character_octet_length         字符最大长度（以字节计）          128                       多字节字符时的长度 <br/>
 * numeric_precision              数值精度（有效位数）              32                       对于数值类型，表示有效位数 <br/>
 * numeric_precision_radix        数值进制                        2                        通常为 2（二进制）或 10（十进制） <br/>
 * numeric_scale                  数值的小数位数                   0                        表示小数部分的位数 <br/>
 * datetime_precision             时间精度                        6                        对于时间类型，表示秒的小数位数 <br/>
 * interval_type                  间隔类型                        NULL                     如果是间隔（INTERVAL）类型，显示具体间隔类型 <br/>
 * interval_precision             间隔精度                        NULL                     间隔类型的精度 <br/>
 * character_set_catalog          字符集所属数据库                 NULL                     通常为 NULL（PostgreSQL 默认不设置） <br/>
 * character_set_schema           字符集所属模式                   NULL                     通常为 NULL <br/>
 * character_set_name             字符集名称                      UTF8                      通常为 UTF8 或 SQL_ASCII <br/>
 * collation_catalog              排序规则所属数据库                NULL                     如果设置了排序规则，显示所属数据库 <br/>
 * collation_schema               排序规则所属模式                  NULL                     如果设置了排序规则，显示所属模式 <br/>
 * collation_name                 排序规则名称                    NULL                      如果设置了排序规则，显示名称 <br/>
 * domain_catalog                 域所属数据库                    NULL                      如果列是基于域（DOMAIN），显示所属数据库 <br/>
 * domain_schema                  域所属模式                      NULL                      如果列是基于域（DOMAIN），显示所属模式 <br/>
 * domain_name                    域名称                         NULL                      如果列是基于域（DOMAIN），显示域名称 <br/>
 * udt_catalog                    UDT（用户定义类型）所属数据库      support                   如果列是 UDT 类型，显示所属数据库 <br/>
 * udt_schema                     UDT 所属模式                    pg_catalog                如果列是 UDT 类型，显示所属模式 <br/>
 * udt_name                       UDT 名称                        varchar                  如果列是 UDT 类型，显示类型名称 <br/>
 * scope_catalog                  作用域所属数据库                 NULL                      用于引用类型，PostgreSQL 通常不设置 <br/>
 * scope_schema                   作用域所属模式                   NULL                      用于引用类型，PostgreSQL 通常不设置 <br/>
 * scope_name                     作用域名称                      NULL                      用于引用类型，PostgreSQL 通常不设置 <br/>
 * maximum_cardinality            最大基数                        NULL                     对于数组类型，表示最大长度 <br/>
 * dtd_identifier                 DTD 标识符                       1                       标识列的唯一编号 <br/>
 * is_self_referencing            是否为自引用                      NO                      如果为自引用列，则为 YES <br/>
 * is_identity                    是否为 IDENTITY 列               NO                      如果是 IDENTITY 列，则为 YES <br/>
 * identity_generation            IDENTITY 列的生成类型            NULL                     ALWAYS 或 BY DEFAULT <br/>
 * identity_start                 IDENTITY 的起始值                NULL                    IDENTITY 列的初始值 <br/>
 * identity_increment             IDENTITY 的增量                NULL                      IDENTITY 列的增量 <br/>
 * identity_maximum               IDENTITY 的最大值                NULL                    IDENTITY 列的最大值 <br/>
 * identity_minimum               IDENTITY 的最小值                NULL                    IDENTITY 列的最小值 <br/>
 * identity_cycle                 是否循环使用IDENTITY值            NO                       如果为 YES，达到最大值时会重置 <br/>
 * is_generated                   是否为生成列                     NEVER                    如果是生成列，则显示 ALWAYS <br/>
 * generation_expression          生成表达式                       NULL                    生成列的表达式 <br/>
 * is_updatable                   列是否可更新                      YES                     如果列允许更新，则为 YES <br/>
 * comment                        列的注释                         主键                     列的注释信息 <br/>
 */
@SuperBuilder
@NoArgsConstructor
@Data
public class ColumnDefine extends DefineBase.Table {

  /**
   * 列名
   */
  @JSONField(name = "column_name")
  @JsonProperty("column_name")
  private String columnName;

  /**
   * 是否为主键
   */
  @JSONField(name = "primary_key")
  @JsonProperty("primary_key")
  private Boolean primaryKey;

  /**
   * 列在表中的位置（序号）	从 1 开始的索引
   */
  @JSONField(name = "ordinal_position")
  @JsonProperty("ordinal_position")
  private Integer ordinalPosition;

  /**
   * 列的默认值	CURRENT_TIMESTAMP	如果列有默认值，显示默认值
   */
  @ApiModelProperty("默认值，如果是取当前时间，则为\"CURRENT_TIMESTAMP\"，其他则根据数据库支持的来定义")
  @JSONField(name = "column_default")
  @JsonProperty("column_default")
  private String columnDefault;

  /**
   * 是否为空 YES / NO
   */
  @ApiModelProperty("是否为空 YES / NO")
  @JSONField(name = "is_nullable")
  @JsonProperty("is_nullable")
  private String isNullable;

  /**
   * 数据类型, {@link #udtName}
   */
  @ApiModelProperty("数据类型")
  @JSONField(name = "data_type")
  @JsonProperty("data_type")
  private String dataType;

  /**
   * 字符最大长度
   */
  @ApiModelProperty("字符最大长度")
  @JSONField(name = "character_maximum_length")
  @JsonProperty("character_maximum_length")
  private Integer characterMaximumLength;

  /**
   * 字符最大长度（以字节计）
   */
  @JSONField(name = "character_octet_length")
  @JsonProperty("character_octet_length")
  private Integer characterOctetLength;

  /**
   * 整数部分的位数
   * 值类型（如 NUMERIC、DECIMAL）的总位数（整数部分 + 小数部分）
   * 对于 NUMERIC(10, 2)，numeric_precision 为 10（总共 10 位数字）
   */
  @ApiModelProperty("整数部分的位数(精确控制小数点后位数)，数据库类型仅支持(NUMERIC、DECIMAL)")
  @JSONField(name = "numeric_precision")
  @JsonProperty("numeric_precision")
  private Integer numericPrecision;

  /**
   * 数值进制，通常是 2（二进制）或 10（十进制）。
   * 如果为 2，则 numeric_precision 表示的是二进制位数（如 INTEGER 的 32 位）。
   * 如果为 10，则 numeric_precision 表示的是十进制位数（如 NUMERIC 的精度）。
   */
  @JSONField(name = "numeric_precision_radix")
  @JsonProperty("numeric_precision_radix")
  private Integer numericPrecisionRadix;

  /**
   * 数值的小数位数
   */
  @JSONField(name = "numeric_scale")
  @JsonProperty("numeric_scale")
  private Integer numericScale;

  /**
   * 表示 时间类型（如 TIMESTAMP、TIME）的小数秒精度（即秒的小数部分位数）。
   * 对于 TIMESTAMP(3)，datetime_precision 为 3（表示毫秒级精度，如 2023-01-01 12:00:00.123）。
   * 对于 TIMESTAMP（无显式精度），默认值通常为 6（微秒级精度）。
   */
  @JSONField(name = "datetime_precision")
  @JsonProperty("datetime_precision")
  private Integer datetimePrecision;

  /**
   * 间隔类型
   */
  @JSONField(name = "interval_type")
  @JsonProperty("interval_type")
  private Object intervalType;

  /**
   * 间隔精度
   */
  @JSONField(name = "interval_precision")
  @JsonProperty("interval_precision")
  private Object intervalPrecision;

  /**
   * 字符集所属数据库
   */
  @JSONField(name = "character_set_catalog")
  @JsonProperty("character_set_catalog")
  private String characterSetCatalog;

  /**
   * 字符集所属模式
   */
  @JSONField(name = "character_set_schema")
  @JsonProperty("character_set_schema")
  private String characterSetSchema;

  /**
   * 字符集名称
   */
  @JSONField(name = "character_set_name")
  @JsonProperty("character_set_name")
  private String characterSetName;

  /**
   * 排序规则所属数据库
   */
  @JSONField(name = "collation_catalog")
  @JsonProperty("collation_catalog")
  private String collationCatalog;

  /**
   * 排序规则所属模式
   */
  @JSONField(name = "collation_schema")
  @JsonProperty("collation_schema")
  private String collationSchema;

  /**
   * 排序规则名称
   */
  @JSONField(name = "collation_name")
  @JsonProperty("collation_name")
  private String collationName;

  /**
   * 域所属数据库
   */
  @JSONField(name = "domain_catalog")
  @JsonProperty("domain_catalog")
  private String domainCatalog;

  /**
   * 域所属模式
   */
  @JSONField(name = "domain_schema")
  @JsonProperty("domain_schema")
  private String domainSchema;

  /**
   * 域名称
   */
  @JSONField(name = "domain_name")
  @JsonProperty("domain_name")
  private String domainName;

  /**
   * UDT（用户定义类型）所属数据库
   */
  @JSONField(name = "udt_catalog")
  @JsonProperty("udt_catalog")
  private String udtCatalog;

  /**
   * UDT 所属模式
   */
  @JSONField(name = "udt_schema")
  @JsonProperty("udt_schema")
  private String udtSchema;

  /**
   * 通用数据类型
   * UDT 名称
   */
  @JSONField(name = "udt_name")
  @JsonProperty("udt_name")
  private String udtName;

  /**
   * 作用域所属数据
   */
  @JSONField(name = "scope_catalog")
  @JsonProperty("scope_catalog")
  private Object scopeCatalog;

  /**
   * 作用域所属模式
   */
  @JSONField(name = "scope_schema")
  @JsonProperty("scope_schema")
  private Object scopeSchema;

  /**
   * 作用域名称
   */
  @JSONField(name = "scope_name")
  @JsonProperty("scope_name")
  private Object scopeName;

  /**
   * 最大基数
   * 对于数组类型，表示最大长度
   */
  @JSONField(name = "maximum_cardinality")
  @JsonProperty("maximum_cardinality")
  private Object maximumCardinality;

  /**
   * DTD 标识符
   * 标识列的唯一编号
   */
  @JSONField(name = "dtd_identifier")
  @JsonProperty("dtd_identifier")
  private String dtdIdentifier;

  /**
   * 是否为自引用 YES/NO
   */
  @JSONField(name = "is_self_referencing")
  @JsonProperty("is_self_referencing")
  private String isSelfReferencing;

  /**
   * 是否为 IDENTITY 列 YES/NO
   */
  @JSONField(name = "is_identity")
  @JsonProperty("is_identity")
  private String isIdentity;

  /**
   * IDENTITY 列的生成类型	ALWAYS 或 BY DEFAULT
   */
  @JSONField(name = "identity_generation")
  @JsonProperty("identity_generation")
  private String identityGeneration;

  /**
   * IDENTITY 的起始值
   * identity_start 是与 标识列（Identity Column） 相关的属性，用于定义自增列的起始值。
   */
  @JSONField(name = "identity_start")
  @JsonProperty("identity_start")
  private Object identityStart;

  /**
   * IDENTITY 的增量
   */
  @JSONField(name = "identity_increment")
  @JsonProperty("identity_increment")
  private Object identityIncrement;

  /**
   * IDENTITY 的最大值
   */
  @JSONField(name = "identity_maximum")
  @JsonProperty("identity_maximum")
  private Object identityMaximum;

  /**
   * IDENTITY 的最小值
   */
  @JSONField(name = "identity_minimum")
  @JsonProperty("identity_minimum")
  private Object identityMinimum;

  /**
   * 是否循环使用 IDENTITY 值
   */
  @JSONField(name = "identity_cycle")
  @JsonProperty("identity_cycle")
  private String identityCycle;

  /**
   * 是否为生成列
   * 如果是生成列，则显示 ALWAYS
   * NEVER
   */
  @JSONField(name = "is_generated")
  @JsonProperty("is_generated")
  private String isGenerated;

  /**
   * 生成表达式
   */
  @JSONField(name = "generation_expression")
  @JsonProperty("generation_expression")
  private Object generationExpression;

  /**
   * 是否可更新 YES/NO
   */
  @JSONField(name = "is_updatable")
  @JsonProperty("is_updatable")
  private String isUpdatable;

  /**
   * 字段注释
   */
  @ApiModelProperty("字段注释")
  @JsonProperty("column_comment")
  private String columnComment;

}
