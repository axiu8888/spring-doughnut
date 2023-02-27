package com.benefitj.minio;

import lombok.Data;

@Data
public class MinIOOptions {

  /**
   * 服务端：https://127.0.0.1
   */
  private String endpoint;
  /**
   * 端口：9001、9006
   */
  private Integer port;
  /**
   * 访问域
   */
  private String region;
  /**
   * 访问秘钥：Ei4H6nMGYs9NcISp
   */
  private String accessKey;
  /**
   * 安全秘钥：CYMQoY59q3NCgp4fbYnmiFnZNzXjs76T
   */
  private String secretKey;

}
