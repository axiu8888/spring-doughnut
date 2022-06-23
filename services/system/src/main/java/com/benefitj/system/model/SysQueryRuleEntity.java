package com.benefitj.system.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.scaffold.base.BaseUuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 规则
 */
@ApiModel("查询规则")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_query_rule")
@TableName("sys_query_rule")
public class SysQueryRuleEntity extends BaseUuidEntity {

  /**
   * 对应的菜单id
   */
  @ApiModelProperty("对应的权限id")
  @Column(name = "permission_id", columnDefinition = "varchar(32) comment '对应的权限id'")
  private String permissionId;

  /**
   * 规则名称
   */
  @ApiModelProperty("规则名称")
  @Column(name = "name", columnDefinition = "varchar(50) comment '规则名称'")
  private String name;

  /**
   * 字段
   */
  @ApiModelProperty("字段")
  @TableField("_column")
  @Column(name = "_column", columnDefinition = "varchar(50) comment '字段'")
  private String column;

  /**
   * 规则值
   */
  @ApiModelProperty("规则值")
  @Column(name = "value", columnDefinition = "varchar(50) comment '规则值'")
  private String value;

  /**
   * 条件
   */
  @ApiModelProperty("条件")
  @Column(name = "conditions", columnDefinition = "varchar(50) comment '条件'")
  private String conditions;

}
