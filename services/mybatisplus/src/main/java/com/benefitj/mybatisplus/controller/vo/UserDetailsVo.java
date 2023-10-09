package com.benefitj.mybatisplus.controller.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.benefitj.mybatisplus.entity.SysAccount;
import com.benefitj.spring.security.jwt.JwtUserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.List;

@ApiModel("用户信息")
public class UserDetailsVo extends SysAccount implements JwtUserDetails {

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
