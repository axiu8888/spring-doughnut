package com.benefitj.system.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.scaffold.base.BaseIdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@ApiModel("日志")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_log", indexes = {
    @Index(name = "idx_org", columnList = "org_id"),
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_module", columnList = "module"),
    @Index(name = "idx_http_method", columnList = "http_method"),
})
@TableName("sys_log")
public class SysLogEntity extends BaseIdEntity implements IOrgEntity {

  @ApiModelProperty("机构ID")
  @Column(name = "org_id", columnDefinition = "varchar(32) comment '机构ID'")
  private String orgId;

  @ApiModelProperty("用户ID")
  @Column(name = "user_id", columnDefinition = "varchar(32) comment '用户ID'")
  private String userId;

  @ApiModelProperty("模块")
  @Column(name = "module", columnDefinition = "varchar(50) comment '模块'")
  private String module;

  @ApiModelProperty("操作描述")
  @Column(name = "op_desc", columnDefinition = "varchar(100) comment '操作描述'")
  private String opDesc;

  @ApiModelProperty("类和方法")
  @Column(name = "class_method", columnDefinition = "varchar(100) comment 'Class和Method'")
  private String classMethod;

  @ApiModelProperty("IP地址")
  @Column(name = "ip_addr", columnDefinition = "varchar(50) comment 'IP地址'")
  private String ipAddr;

  @ApiModelProperty("请求路径")
  @Column(name = "url", columnDefinition = "text comment '请求路径'")
  private String url;

  @ApiModelProperty("请求方法")
  @Column(name = "http_method", columnDefinition = "varchar(20) comment '请求方法'")
  private String httpMethod;

  @ApiModelProperty("请求参数，JSON")
  @Column(name = "parameters", columnDefinition = "text comment '请求参数，JSON'")
  private String parameters;

  @ApiModelProperty("结果：成功或错误")
  @Column(name = "result", columnDefinition = "text comment '结果：成功或错误'")
  private String result;

  @ApiModelProperty("状态码")
  @Column(name = "status_code", columnDefinition = "smallint comment '状态码'")
  private Integer statusCode;

  @ApiModelProperty("耗时")
  @Column(name = "elapsed", columnDefinition = "int comment '耗时'")
  private Long elapsed;

}
