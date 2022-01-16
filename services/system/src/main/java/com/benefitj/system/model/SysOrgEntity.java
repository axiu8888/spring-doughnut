package com.benefitj.system.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.scaffold.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@TableName("sys_orgs")
@Table(name = "sys_org", indexes = {
    @Index(name = "idx_pid_name", columnList = "pid, name"),
})
public class SysOrgEntity extends BaseEntity {

  public static final String AUTO = ":";

  /**
   * 机构 ID
   */
  @ApiModelProperty("机构ID")
  @Id
  @Column(name = "id", columnDefinition = "varchar(32) comment '机构ID'", length = 32)
  private String id;
  /**
   * 父级机构 ID
   */
  @ApiModelProperty("父级机构ID")
  @Column(name = "pid", columnDefinition = "varchar(32) comment '父级机构ID'", length = 32)
  private String pid;
  /**
   * 机构名
   */
  @ApiModelProperty("机构名")
  @Column(name = "name", columnDefinition = "varchar(100) comment '机构名'", length = 100)
  private String name;
  /**
   * code或id 生成的路径，包含自身的code或id(具体取什么值根据业务决定)
   */
  @ApiModelProperty("生成的路径")
  @Column(name = "auto_code", columnDefinition = "varchar(1024) comment '生成的路径'", length = 1024)
  private String autoCode;
  /**
   * 机构号
   */
  @ApiModelProperty("机构号")
  @Column(name = "code", columnDefinition = "varchar(30) comment '机构号'", length = 30)
  private String code;
  /**
   * 机构LOGO
   */
  @ApiModelProperty("机构LOGO")
  @Column(name = "logo", columnDefinition = "varchar(1024) comment '机构LOGO'", length = 1024)
  private String logo;
  /**
   * 创建者
   */
  @ApiModelProperty("创建者")
  @Column(name = "creatorId", columnDefinition = "varchar(32) comment '创建者'", length = 32)
  private String creatorId;

  /**
   * 获取父节点的 autoCode
   */
  @JSONField(serialize = false)
  @JsonIgnore
  public String getParentAutoCode() {
    String autoCode = getAutoCode();
    return getParentAutoCode(autoCode);
  }

  /**
   * 生成 autoCode
   *
   * @param parentAutoCode 父节点的 autoCode
   * @param code           code 或 id
   * @return 返回生成的 autoCode
   */
  public static String generateAutoCode(String parentAutoCode, String code) {
    return isNotEmpty(parentAutoCode) ? (parentAutoCode + ":" + code) : code;
  }

  /**
   * 获取父节点的 autoCode
   *
   * @param autoCode 当前的 autoCode
   * @return 返回父节点的 autoCode
   */
  public static String getParentAutoCode(String autoCode) {
    return isNotEmpty(autoCode) && autoCode.contains(AUTO)
        ? autoCode.substring(0, autoCode.lastIndexOf(AUTO)) : autoCode;
  }

  private static boolean isNotEmpty(String str) {
    return str != null && !str.isEmpty();
  }

}
