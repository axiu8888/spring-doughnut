package com.benefitj.scaffold.security;

import com.benefitj.scaffold.security.token.JwtToken;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.scaffold.security.user.JwtUserDetails;

import javax.annotation.Nullable;

/**
 * 当前用户管理
 */
public interface CurrentUserService {

  /**
   * 获取当前的 token，如果当前请求未认证，则返回 NULL
   */
  @Nullable
  default JwtToken currentToken() {
    return JwtTokenManager.currentToken(true);
  }

  /**
   * 获取用户ID，如果当前请求未认证，则返回 NULL
   */
  @Nullable
  default String currentUserId() {
    return JwtTokenManager.currentUserId();
  }

  /**
   * 获取机构ID，如果当前请求未认证，则返回 NULL
   */
  @Nullable
  default String currentOrgId() {
    return JwtTokenManager.currentOrgId();
  }

  /**
   * 获取用户信息详情，如果当前请求未认证，则返回 NULL
   */
  @Nullable
  default JwtUserDetails currentUserDetails() {
    return JwtTokenManager.currentUserDetails();
  }

}
