package com.benefitj.scaffold.base;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class BaseUuidEntity extends BaseEntity {

  /**
   * 创建时间
   */
  @ApiModelProperty("主键ID")
  @TableId(type = IdType.ASSIGN_UUID)
  @Id
  @Column(name = "id", columnDefinition = "varchar(32) comment '主键ID'")
  private String id;

}
