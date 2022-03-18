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
 * 用户和角色关联表
 */
@ApiModel("用户和角色关联表")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_user_role", indexes = {
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_role", columnList = "role_id"),
})
@TableName("sys_user_role")
public class SysUserRoleEntity extends BaseIdEntity {

  /**
   * 用户ID
   */
  @ApiModelProperty("用户ID")
  @Column(name = "user_id", columnDefinition = "varchar(32) comment '用户ID'", length = 32, nullable = false)
  private String userId;
  /**
   * 角色ID
   */
  @ApiModelProperty("角色ID")
  @Column(name = "role_id", columnDefinition = "varchar(32) comment '角色ID'", length = 32, nullable = false)
  private String roleId;

}
