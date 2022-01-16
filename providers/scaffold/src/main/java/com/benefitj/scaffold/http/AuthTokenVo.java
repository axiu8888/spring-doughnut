package com.benefitj.scaffold.http;


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

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
