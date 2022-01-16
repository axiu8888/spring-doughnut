package com.benefitj.scaffold.security.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface JwtUserDetails extends UserDetails {

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

}
