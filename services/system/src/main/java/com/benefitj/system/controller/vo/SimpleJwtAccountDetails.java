package com.benefitj.system.controller.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.benefitj.system.model.SysAccountEntity;
import com.benefitj.scaffold.security.user.JwtUserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.List;

/**
 * JWT用户详情
 */
public class SimpleJwtAccountDetails extends SysAccountEntity implements JwtUserDetails {

  /**
   * 权限
   */
  private List<GrantedAuthority> authorities = Collections.emptyList();
  /**
   * 机构ID
   */
  private String orgId;

  /**
   * 获取用户ID
   */
  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  @Override
  public String getUserId() {
    return getId();
  }

  /**
   * 设置用户ID
   *
   * @param userId 用户ID
   */
  @JsonIgnore
  @JSONField(serialize = false, deserialize = false)
  @Override
  public void setUserId(String userId) {
    this.setId(userId);
  }

  @Override
  public String getOrgId() {
    return orgId;
  }

  @Override
  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  @Override
  public void setAuthorities(List<GrantedAuthority> authorities) {
    this.authorities = authorities;
  }

  /**
   * 获取权限
   */
  @Override
  public List<GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return Boolean.FALSE.equals(getLocked());
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return Boolean.TRUE.equals(getActive());
  }
}
