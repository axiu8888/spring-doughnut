package com.benefitj.system.service;

import com.benefitj.system.mapper.SysUserAndRoleMapper;
import com.benefitj.system.model.SysRoleEntity;
import com.benefitj.system.model.SysUserAndRoleEntity;
import com.benefitj.scaffold.base.BaseService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 用户和角色关联
 */
@Service
public class SysUserAndRoleService extends BaseService<SysUserAndRoleMapper, SysUserAndRoleEntity> {

  /**
   * 统计用户是否拥有角色
   *
   * @param userId 用户ID
   * @param roles  角色ID
   * @return 返回统计的角色的条数
   */
  public long countRoleByUser(String userId, String... roles) {
    return getBaseMapper().countRoleByUser(userId, Arrays.asList(roles));
  }

  /**
   * 统计角色关联的用户
   *
   * @param roles 角色ID
   * @return 返回统计的角色的条数
   */
  public long countUserByRoles(List<? extends Serializable> roles) {
    return getBaseMapper().countUserByRole(roles);
  }

  public int delete(SysUserAndRoleEntity condition) {
    return getBaseMapper().delete(qw(condition));
  }

  /**
   * 通过用户获取关联的角色
   *
   * @param userId 用户ID
   * @return 返回用户拥有的角色
   */
  public List<SysRoleEntity> getRoleByUserId(String userId) {
    return getBaseMapper().findByUser(userId);
  }

  /**
   * 删除用户关联的全部角色
   *
   * @param userId 用户ID
   * @param roles  角色ID
   * @return 返回删除的条数
   */
  public int deleteAll(String userId, List<String> roles) {
    return getBaseMapper().deleteAll(userId, roles);
  }

}
