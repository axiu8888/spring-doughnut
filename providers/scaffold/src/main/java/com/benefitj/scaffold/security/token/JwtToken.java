package com.benefitj.scaffold.security.token;

import com.benefitj.scaffold.security.user.JwtUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface JwtToken extends IJwt<Claims>, Authentication {

  /**
   * 是否为 refresh token
   */
  default boolean isRefresh() {
    return Boolean.TRUE.equals(get("refresh", Boolean.class));
  }

  /**
   * 设置是否为 refresh token
   *
   * @param refresh
   */
  default void setRefresh(boolean refresh) {
    put("refresh", refresh);
  }

  /**
   * 获取原始 token
   */
  default String getRawToken() {
    return get("rawToken", String.class);
  }

  /**
   * 设置原始 token
   *
   * @param rawToken
   */
  default void setRawToken(String rawToken) {
    put("rawToken", rawToken);
  }

  /**
   * 获取用户ID
   */
  default String getUserId() {
    return get("userId", String.class);
  }

  /**
   * 设置用户ID
   *
   * @param userId 用户ID
   */
  default void setUserId(String userId) {
    put("userId", userId);
  }

  /**
   * 获取机构ID
   */
  default String getOrgId() {
    return get("orgId", String.class);
  }

  /**
   * 设置机构ID
   *
   * @param orgId 机构ID
   */
  default void setOrgId(String orgId) {
    put("orgId", orgId);
  }

  /**
   * 获取 userDetails
   */
  JwtUserDetails getUserDetails();

  /**
   * 设置 userDetails
   *
   * @param userDetails
   */
  void setUserDetails(JwtUserDetails userDetails);

  /**
   * 获取权限
   */
  @Override
  List<GrantedAuthority> getAuthorities();

  /**
   * 权限
   *
   * @param authorities
   */
  void setAuthorities(List<GrantedAuthority> authorities);

  @Override
  default boolean isAuthenticated() {
    return Boolean.TRUE.equals(get("authenticated", Boolean.class));
  }

  @Override
  default void setAuthenticated(boolean isAuthenticated) {
    put("authenticated", isAuthenticated);
  }

  /**
   * 拷贝
   */
  JwtToken copy();

}
