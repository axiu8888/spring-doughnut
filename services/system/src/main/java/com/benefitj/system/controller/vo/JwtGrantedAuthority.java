package com.benefitj.system.controller.vo;

import com.benefitj.system.model.SysRoleEntity;
import org.springframework.security.core.GrantedAuthority;

/**
 * 权限
 */
public class JwtGrantedAuthority extends SysRoleEntity implements GrantedAuthority {

  @Override
  public String getAuthority() {
    return getId();
  }

}
