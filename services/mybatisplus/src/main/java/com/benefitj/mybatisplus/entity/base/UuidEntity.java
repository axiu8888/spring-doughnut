package com.benefitj.mybatisplus.entity.base;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@SuperBuilder
@NoArgsConstructor
@Data
@MappedSuperclass
public abstract class UuidEntity extends EntityBase {

  /**
   * ID
   */
  @ApiModelProperty(value = "ID")
  @TableId(type = IdType.ASSIGN_UUID)
  @Id
  @GeneratedValue
  @Column(name = "id", columnDefinition = "varchar(32) comment '主键ID'", length = 32)
  private String id;

}
