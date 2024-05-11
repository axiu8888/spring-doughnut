package com.benefitj.mybatisplus.entity.mysql.base;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.sql.Timestamp;

@SuperBuilder
@NoArgsConstructor
@Data
public abstract class MysqlBase implements Serializable {

  @Transient
  Timestamp createTime;

  @Transient
  Timestamp updateTime;

}
