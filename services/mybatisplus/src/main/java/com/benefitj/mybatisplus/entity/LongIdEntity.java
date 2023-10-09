package com.benefitj.mybatisplus.entity;


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
public abstract class LongIdEntity extends EntityBase {

  /**
   * ID
   */
  @ApiModelProperty(value = "主键")
  @TableId(type = IdType.ASSIGN_ID)
  @Id
  @GeneratedValue
  @Column(name = "id", columnDefinition = "bigint comment '主键ID'")
  private Long id;

}
