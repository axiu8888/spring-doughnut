package com.benefitj.mybatisplus.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.mybatisplus.entity.base.UuidEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * 账号
 */
@ApiModel("账号")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_account")
@TableName("sys_account")
public class SysAccount extends UuidEntity {

  /**
   * 账号ID
   */
  @ApiModelProperty("账号ID")
  @Column(name = "user_id", columnDefinition = "varchar(32) comment '用户ID'", nullable = false)
  private String userId;
  /**
   * 用户名
   */
  @ApiModelProperty("用户名")
  @PrimaryKeyJoinColumn
  @Column(name = "username", columnDefinition = "varchar(100) comment '用户名'", length = 100, nullable = false, unique = true)
  private String username;
  /**
   * 密码
   */
  @ApiModelProperty("密码")
  @Column(name = "password", columnDefinition = "varchar(200) comment '密码'", length = 200, nullable = false)
  private String password;
  /**
   * 是否被锁住
   */
  @ApiModelProperty("是否被锁住")
  @Column(name = "locked", columnDefinition = "tinyint(1) NOT NULL DEFAULT 0 comment '是否被锁住'")
  private Boolean locked;

}

