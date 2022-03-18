package com.benefitj.system.service;

import com.benefitj.core.IdUtils;
import com.benefitj.system.mapper.SysRoleMapper;
import com.benefitj.system.model.SysRoleEntity;
import com.benefitj.system.model.SysUserRoleEntity;
import com.benefitj.scaffold.base.BaseService;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

/**
 * 系统角色
 */
@Service
public class SysRoleService extends BaseService<SysRoleMapper, SysRoleEntity> {


  @Autowired
  private SysUserAndRoleService uarService;

  /**
   * 创建角色
   *
   * @param role 角色信息
   */
  public SysRoleEntity create(SysRoleEntity role) {
    role.setId(IdUtils.uuid());
    role.setOrgId(StringUtils.isNotBlank(role.getOrgId()) ? role.getOrgId() : JwtTokenManager.currentOrgId());
    role.setActive(Boolean.TRUE);
    getBaseMapper().insert(role);
    return role;
  }

  /**
   * 更新角色
   *
   * @param role 角色信息
   * @return 返回更新的数据
   */
  public SysRoleEntity update(SysRoleEntity role) {
    SysRoleEntity existRole = getById(role.getId());
    if (existRole == null) {
      throw new SysException("无法发现角色");
    }
    existRole.setName(role.getName());
    existRole.setRemarks(role.getRemarks());
    super.updateById(existRole);
    return existRole;
  }

  /**
   * 删除角色
   *
   * @param roleId 角色ID
   * @return 返回删除条数，如果被删除成功，应该返回 1, 否则返回 0
   */
  @Override
  public int deleteById(Serializable roleId) {
    SysRoleEntity role = getById(roleId);
    if (role != null) {
      // 检查被关联的用户
      if (uarService.countUserByRoles(Collections.singletonList(roleId)) > 0) {
        // 强制删除关联的角色信息
        uarService.delete(SysUserRoleEntity.builder()
            .roleId((String) roleId)
            .build());
      }
      return getBaseMapper().deleteById(role.getId());
    }
    return 0;
  }

  /**
   * 改变角色可用状态
   *
   * @param id     角色ID
   * @param active 状态
   * @return 返回是否更新
   */
  public boolean changeActive(String id, Boolean active) {
    SysRoleEntity role = getById(id);
    if (role != null) {
      role.setActive(active != null ? active : role.getActive());
      return updateById(role);
    }
    return false;
  }

}
