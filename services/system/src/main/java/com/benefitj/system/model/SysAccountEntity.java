package com.benefitj.system.model;

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
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 账号
 */
@ApiModel("账号")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_account", indexes = {
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_username", columnList = "username"),
})
@TableName("sys_account")
public class SysAccountEntity extends BaseUuidEntity {

  /**
   * 用户ID
   */
  @ApiModelProperty("用户ID")
  @Column(name = "user_id", columnDefinition = "varchar(32) comment '用户ID'")
  private String userId;
  /**
   * 用户名
   */
  @ApiModelProperty("用户名")
  @Column(name = "username", columnDefinition = "varchar(100) comment '用户名'")
  private String username;
  /**
   * 密码
   */
  @ApiModelProperty("密码")
  @Column(name = "password", columnDefinition = "varchar(200) comment '密码'")
  private String password;
//  /**
//   * 是否被锁住
//   */
//  @ApiModelProperty("是否被锁住")
//  @Column(name = "locked", columnDefinition = "tinyint(1) NOT NULL DEFAULT 0 comment '是否被锁住'")
//  private Boolean locked;

}
