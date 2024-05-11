package com.benefitj.mybatisplus.entity.postgresql.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * UUID做主键的基类
 */
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class HsUuidEntity extends PostgresqlBase {

  /**
   * 主键id
   */
  @ApiModelProperty(value = "主键id")
  @TableId(type = IdType.ASSIGN_UUID)
  @GeneratedValue()
  @Id
  @Column(name = "id", columnDefinition = "varchar(32) comment '主键ID'")
  private String id;

}
