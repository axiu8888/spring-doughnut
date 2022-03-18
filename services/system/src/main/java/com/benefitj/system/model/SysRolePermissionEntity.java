package com.benefitj.system.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.scaffold.base.BaseIdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 角色和权限关联表
 */
@ApiModel("角色和权限关联表")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_role_permission", indexes = {
    @Index(name = "idx_role", columnList = "role_id"),
    @Index(name = "idx_permission", columnList = "permission_id"),
})
@TableName("sys_role_permission")
public class SysRolePermissionEntity extends BaseIdEntity {

  /**
   * 角色ID
   */
  @ApiModelProperty("角色ID")
  @Column(name = "role_id", columnDefinition = "varchar(32) comment '角色ID'", length = 32, nullable = false)
  private String roleId;
  /**
   * 权限ID
   */
  @ApiModelProperty("权限ID")
  @Column(name = "permission_id", columnDefinition = "varchar(32) comment '权限ID'", length = 32, nullable = false)
  private String permissionId;

}
