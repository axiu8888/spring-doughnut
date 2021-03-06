package com.benefitj.scaffold.base;


import com.baomidou.mybatisplus.annotation.Version;
import com.benefitj.scaffold.mybatis.FillValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.mapping.SqlCommandType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@MappedSuperclass
public abstract class BaseEntity {

  /**
   * 创建时间
   */
  @FillValue(key = "createTime", types = SqlCommandType.INSERT)
  @ApiModelProperty(value = "创建时间: yyyy-MM-dd HH:mm:ss", dataType = "String")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "create_time", columnDefinition = "datetime comment '创建时间'")
  private Date createTime;
  /**
   * 创建者
   */
  @FillValue(key = "createBy", types = SqlCommandType.INSERT)
  @ApiModelProperty("创建者")
  @Column(name = "create_by", columnDefinition = "varchar(32) comment '创建者'")
  private String createBy;
  /**
   * 修改时间
   */
  @FillValue(key = "updateTime", types = SqlCommandType.UPDATE)
  @ApiModelProperty(value = "修改时间: yyyy-MM-dd HH:mm:ss", dataType = "String")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "update_time", columnDefinition = "datetime DEFAULT NULL ON UPDATE current_timestamp() comment '修改时间'")
  private Date updateTime;
  /**
   * 更新者
   */
  @FillValue(key = "updateBy", types = SqlCommandType.UPDATE)
  @ApiModelProperty("更新者")
  @Column(name = "update_by", columnDefinition = "varchar(32) comment '更新者'")
  private String updateBy;

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
  @ApiModelProperty("是否可用，默认可用")
  @Column(name = "active", columnDefinition = "tinyint(1) NOT NULL DEFAULT 1 comment '是否可用，默认可用'")
  private Boolean active;

  /**
   * 逻辑删除的状态
   */
  @FillValue(key = "deleted", types = SqlCommandType.DELETE)
  @ApiModelProperty("逻辑删除的状态: 否(0)/是(1)")
  @Column(name = "deleted", columnDefinition = "tinyint(1) NOT NULL DEFAULT 1 comment '逻辑删除的状态: 否(0)/是(1)'")
  private Boolean deleted;

}
