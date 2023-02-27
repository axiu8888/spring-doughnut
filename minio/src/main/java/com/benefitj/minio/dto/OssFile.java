package com.benefitj.minio.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class OssFile extends BaseEntity {

  /**
   * 主键
   */
  private Long fileId;

  /**
   * 文件上传后重新命名
   */
  private String fileKey;

  /**
   * 原始文件名
   */
  private String fileName;

  /**
   * 文件夹名称
   */
  private String fileDir;

  /**
   * 文件长度
   */
  private Long fileLength;
  /**
   * 文件扩展名
   */
  private String fileExtension;
  /**
   * 文件业务类型
   */
  private String businessType;

  private String businessTypeName;

  /**
   * 文件业务ID
   */
  private Long businessId;

  /**
   * 文件业务编码
   */
  private String businessNo;

  /**
   * 创建部门
   */
  private Long orgId;

  /**
   * 状态
   */
  private String state;

  /**
   * 删除标志（0代表存在 1代表删除）
   */
  private String dr;

  /**
   * 时间戳
   */
  private Date ts;

  private Long[] fileIds;

  /**
   * 资源访问地址
   */
  private String url;

}
