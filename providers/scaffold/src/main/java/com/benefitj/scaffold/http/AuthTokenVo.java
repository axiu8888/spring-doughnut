package com.benefitj.scaffold.http;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 认证
 *
 * @param <T>
 */
@ApiModel("认证")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthTokenVo<T> {

  /**
   * access token
   */
  private String accessToken;
  /**
   * refresh token
   */
  private String refreshToken;
  /**
   * 其他数据
   */
  private T data;

}
