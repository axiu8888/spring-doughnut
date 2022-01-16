package com.benefitj.scaffold.security.token;

import com.benefitj.scaffold.security.user.JwtUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface JwtToken extends Jwt<Header, Claims>, Claims, Authentication {

  /**
   * 是否为 refresh token
   */
  boolean isRefresh();

  /**
   * 设置是否为 refresh token
   *
   * @param refresh
   */
  void setRefresh(boolean refresh);

  /**
   * 获取原始 token
   */
  String getRawToken();

  /**
   * 设置原始 token
   *
   * @param rawToken
   */
  void setRawToken(String rawToken);

  /**
   * 获取用户ID
   */
  String getUserId();

  /**
   * 设置用户ID
   *
   * @param userId 用户ID
   */
  void setUserId(String userId);

  /**
   * 获取机构ID
   */
  String getOrgId();

  /**
   * 设置机构ID
   *
   * @param orgId 机构ID
   */
  void setOrgId(String orgId);

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

  /**
   * 拷贝
   */
  JwtToken copy();

}
