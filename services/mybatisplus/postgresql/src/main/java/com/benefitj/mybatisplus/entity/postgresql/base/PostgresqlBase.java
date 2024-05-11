package com.benefitj.mybatisplus.entity.postgresql.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * 基类
 */
@SuperBuilder
@NoArgsConstructor
@Data
@MappedSuperclass
public abstract class PostgresqlBase implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 创建人
   */
  @ApiModelProperty("创建人")
  @Column(name = "create_by", columnDefinition = "varchar(32) comment '创建人'")
  private String createBy;
  /**
   * 创建时间
   */
  @ApiModelProperty("创建时间")
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "create_time", columnDefinition = "datetime comment '创建时间'")
  private Date createTime;
  /**
   * 更新人
   */
  @ApiModelProperty("更新人")
  @Column(name = "update_by", columnDefinition = "varchar(32) comment '更新人'")
  private String updateBy;
  /**
   * 更新时间
   */
  @ApiModelProperty("更新时间")
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "update_time", columnDefinition = "datetime DEFAULT NULL ON UPDATE current_timestamp() comment '更新时间'")
  private Date updateTime;
}
