package com.benefitj.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 系统权限
 */
@ApiModel("系统权限")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableName("sys_permission")
@Table(name = "sys_permission", indexes = {
    @Index(name = "idx_org_name", columnList = "org_id, name"),
})
public class SysPermissionEntity extends BaseEntity {
  /**
   * ID
   */
  @ApiModelProperty("ID")
  @TableId(type = IdType.ASSIGN_UUID)
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
   * 资源类型，请参考：{@link ResourceType}
   */
  @ApiModelProperty("资源类型")
  @Column(name = "resource_type", columnDefinition = "varchar(30) comment '资源类型'")
  private ResourceType resourceType;

  /**
   * 创建者
   */
  @ApiModelProperty("创建者")
  @Column(name = "creator", columnDefinition = "varchar(32) comment '创建者' ", length = 32)
  private String creator;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  @Column(name = "remarks", columnDefinition = "varchar(1024) comment '备注'", length = 1024)
  private String remarks;

}
