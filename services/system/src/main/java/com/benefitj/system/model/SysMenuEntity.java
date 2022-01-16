package com.benefitj.system.model;

import com.benefitj.scaffold.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

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
    @Index(name = "idx_org_name_creator", columnList = "org_id, name, creator"),
})
public class SysMenuEntity extends BaseEntity {
  /**
   * ID
   */
  @ApiModelProperty("ID")
  @Id
  @Column(name = "id", columnDefinition = "varchar(32) comment 'ID'", length = 32)
  private String id;

  /**
   * 所属机构ID
   */
  @ApiModelProperty("所属机构ID")
  @Column(name = "org_id", columnDefinition = "varchar(32) comment '所属机构ID' ", nullable = false, length = 32)
  private String orgId;

  /**
   * 名称
   */
  @ApiModelProperty("名称")
  @Column(name = "name", columnDefinition = "varchar(100) comment '名称' ", length = 100)
  private String name;

  /**
   * 创建者
   */
  @ApiModelProperty("创建者")
  @Column(name = "creator", columnDefinition = "varchar(32) comment '创建者' ", length = 32)
  private String creator;

  /**
   * 父节点
   */
  @ApiModelProperty("父节点")
  @Column(name = "pid", columnDefinition = "varchar(32) comment '父节点'", length = 32)
  private String pid;

  /**
   * 菜单图标
   */
  @ApiModelProperty("菜单图标")
  @Column(name = "icon", columnDefinition = "varchar(1024) comment '菜单图标'", length = 1024)
  private String icon;

  /**
   * 地址
   */
  @ApiModelProperty("地址")
  @Column(name = "uri", columnDefinition = "varchar(1024) comment '地址'", length = 1024)
  private String uri;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  @Column(name = "remarks", columnDefinition = "varchar(1024) comment '备注'", length = 1024)
  private String remarks;

}
