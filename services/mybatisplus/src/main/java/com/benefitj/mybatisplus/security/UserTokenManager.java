package com.benefitj.mybatisplus.security;

public interface UserTokenManager {

  String getToken();

  /**
   * 获取用户ID
   */
  String getUserId();

  /**
   * 获取机构ID
   */
  String getOrgId();
}
