package com.benefitj.mybatisplus.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@MappedSuperclass
public abstract class LongIdEntity extends EntityBase {

  /**
   * ID
   */
  @ApiModelProperty(value = "主键")
  @TableId(type = IdType.ASSIGN_UUID)
  @Id
  @GeneratedValue
  @Column(name = "id", columnDefinition = "bigint comment '主键ID'")
  private String id;

}
