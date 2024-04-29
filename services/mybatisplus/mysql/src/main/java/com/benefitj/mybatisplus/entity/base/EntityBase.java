package com.benefitj.mybatisplus.entity.base;


import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@SuperBuilder
@NoArgsConstructor
@Data
@MappedSuperclass
public abstract class EntityBase {

  /**
   * 创建时间
   */
  @ApiModelProperty(value = "创建时间: yyyy-MM-dd HH:mm:ss", dataType = "String")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "create_time", columnDefinition = "datetime comment '创建时间'")
  private Date createTime;
  /**
   * 修改时间
   */
  @ApiModelProperty(value = "修改时间: yyyy-MM-dd HH:mm:ss", dataType = "String")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "update_time", columnDefinition = "datetime comment '修改时间'")
  private Date updateTime;

  /**
   * 乐观锁
   */
  @ApiModelProperty("乐观锁")
  @Version
  @Column(name = "version", columnDefinition = "int comment '乐观锁'")
  private Integer version;

  /**
   * 是否可用
   */
  @ApiModelProperty("是否可用")
  @Column(name = "active", columnDefinition = "tinyint(2) NOT NULL DEFAULT 1 comment '是否可用'")
  private Boolean active;

}
