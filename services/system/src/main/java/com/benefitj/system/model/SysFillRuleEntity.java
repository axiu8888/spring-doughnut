//package com.benefitj.system.model;
//
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.benefitj.scaffold.base.BaseUuidEntity;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//import lombok.experimental.SuperBuilder;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Table;
//
///**
// * 填值规则
// */
//@ApiModel("填值规则")
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
//@Data
//@Entity
//@Table(name = "sys_fill_rule")
//@TableName("sys_fill_rule")
//public class SysFillRuleEntity extends BaseUuidEntity {
//
//  @ApiModelProperty(value = "名称")
//  @Column(name = "name", columnDefinition = "varchar(50) comment '组件名称'")
//  private String name;
//
//  @ApiModelProperty(value = "规则Code")
//  @Column(name = "code", columnDefinition = "varchar(50) comment '规则Code'")
//  private String code;
//
//  @ApiModelProperty(value = "规则实现类")
//  @Column(name = "clazz", columnDefinition = "varchar(50) comment '规则实现类'")
//  private String clazz;
//
//  @ApiModelProperty(value = "规则参数")
//  @Column(name = "params", columnDefinition = "text comment '规则参数'")
//  private String params;
//
//}
