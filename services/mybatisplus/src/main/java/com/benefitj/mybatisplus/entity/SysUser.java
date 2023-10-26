package com.benefitj.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.benefitj.core.TimeUtils;
import com.benefitj.mybatisplus.entity.base.UuidEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@ApiModel(value = "用户")
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TableName(value = "sys_users")
@Table(name = "sys_users"
    //, indexes = {@Index(name = "idx_org_id", columnList = "org_id")}
)
public class SysUser extends UuidEntity {

  @ApiModelProperty("机构ID")
  @Column(name = "org_id", columnDefinition = "varchar(32) comment '机构ID'", length = 32)
  private String orgId;

  @ApiModelProperty("姓名")
  @Column(name = "name", columnDefinition = "varchar(50) comment '姓名'", length = 50)
  private String name;

  @ApiModelProperty(value = "性别", dataType = "String")
  @Column(name = "gender", columnDefinition = "varchar(6) comment '性别'", length = 6)
  private GenderEnum gender;

  @ApiModelProperty(value = "出生日期: yyyy-MM-dd", dataType = "String")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Column(name = "birthday", columnDefinition = "date comment '出生日期'")
  private Date birthday;

  @ApiModelProperty("是否被锁住")
  @Column(name = "locked", columnDefinition = "tinyint(2) NOT NULL DEFAULT 0 comment '是否被锁住'")
  private Boolean locked;

  public Integer getAge() {
    Date b = getBirthday();
    return b != null ? TimeUtils.getAge(b) : null;
  }
}
