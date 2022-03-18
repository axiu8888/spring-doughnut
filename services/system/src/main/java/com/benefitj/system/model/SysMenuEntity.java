package com.benefitj.system.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.scaffold.base.BaseUuidEntity;
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

/**
 * 系统菜单
 */
@ApiModel("系统菜单")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "sys_menu", indexes = {
    @Index(name = "idx_org_name_createby", columnList = "org_id, name, create_by"),
})
@TableName("sys_menu")
public class SysMenuEntity extends BaseUuidEntity implements IOrgEntity {

  /**
   * 所属机构ID
   */
  @ApiModelProperty("所属机构ID")
  @Column(name = "org_id", columnDefinition = "varchar(32) comment '所属机构ID'")
  private String orgId;

  /**
   * 名称
   */
  @ApiModelProperty("名称")
  @Column(name = "name", columnDefinition = "varchar(100) comment '名称'")
  private String name;
  /**
   * 父节点
   */
  @ApiModelProperty("父节点")
  @Column(name = "pid", columnDefinition = "varchar(32) comment '父节点'")
  private String pid;

  /**
   * 菜单图标
   */
  @ApiModelProperty("菜单图标")
  @Column(name = "icon", columnDefinition = "text comment '菜单图标'")
  private String icon;

  /**
   * 地址
   */
  @ApiModelProperty("地址")
  @Column(name = "uri", columnDefinition = "text comment '地址'")
  private String uri;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  @Column(name = "remarks", columnDefinition = "text comment '备注'")
  private String remarks;

}
