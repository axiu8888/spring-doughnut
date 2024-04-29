package com.benefitj.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.mybatisplus.entity.base.LongIdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@ApiModel("操作日志")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableName("sys_op_logs")
@Table(name = "sys_op_logs"
    //, indexes = {@Index(name = "idx_org_user", columnList = "org_id, user_id"),}
)
public class SysOpLog extends LongIdEntity {

  @ApiModelProperty("机构ID")
  @Column(name = "org_id", columnDefinition = "varchar(32) comment '机构ID'", length = 32)
  private String orgId;

  @ApiModelProperty("用户ID")
  @Column(name = "user_id", columnDefinition = "varchar(32) comment '用户ID'", length = 32)
  private String userId;

  @ApiModelProperty("模块")
  @Column(name = "module", columnDefinition = "varchar(50) comment '模块'", length = 50)
  private String module;

  @ApiModelProperty("操作描述")
  @Column(name = "op_desc", columnDefinition = "varchar(100) comment '操作描述'", length = 100)
  private String opDesc;

  @ApiModelProperty("类和方法")
  @Column(name = "class_method", columnDefinition = "varchar(100) comment 'Class和Method'", length = 100)
  private String classMethod;

  @ApiModelProperty("IP地址")
  @Column(name = "ip_addr", columnDefinition = "varchar(50) comment 'IP地址'", length = 50)
  private String ipAddr;

  @ApiModelProperty("请求路径")
  @Column(name = "url", columnDefinition = "varchar(1024) comment '请求路径'", length = 1024)
  private String url;

  @ApiModelProperty("请求方法")
  @Column(name = "http_method", columnDefinition = "varchar(20) comment '请求方法'", length = 20)
  private String httpMethod;

  @ApiModelProperty("请求参数，JSON")
  @Column(name = "parameters", columnDefinition = "varchar(2048) comment '请求参数，JSON'", length = 2048)
  private String parameters;

  @ApiModelProperty("结果：成功或错误")
  @Column(name = "result", columnDefinition = "varchar(50) comment '结果：成功或错误'", length = 50)
  private String result;

  @ApiModelProperty("状态码")
  @Column(name = "status_code", columnDefinition = "smallint comment '状态码'")
  private Integer statusCode;

  @ApiModelProperty("耗时")
  @Column(name = "elapsed", columnDefinition = "bigint comment '耗时'")
  private Long elapsed;

}
