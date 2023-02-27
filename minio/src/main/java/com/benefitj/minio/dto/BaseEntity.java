package com.benefitj.minio.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author Administrator
 */
@Data
@SuperBuilder
public class BaseEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 请求参数
   */
  @JsonIgnore
  @JSONField(deserialize = false, serialize = false)
  private Map<String, Object> params;
  /**
   * 搜索值
   */
  private String searchValue;

  /**
   * 创建人ID
   */
  private String createBy;
  /**
   * 创建时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
  /**
   * 修改人ID
   */
  private String updateBy;
  /**
   * 更新时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;

  /**
   * 备注
   */
  private String remark;

}
