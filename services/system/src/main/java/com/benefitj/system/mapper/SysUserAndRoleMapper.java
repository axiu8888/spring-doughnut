package com.benefitj.system.mapper;

import com.benefitj.system.model.SysRoleEntity;
import com.benefitj.system.model.SysUserRoleEntity;
import com.benefitj.scaffold.base.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import javax.annotation.Nullable;
import java.beans.Transient;
import java.io.Serializable;
import java.util.List;

/**
 * 用户和角色关联
 */
@Mapper
public interface SysUserAndRoleMapper extends SuperMapper<SysUserRoleEntity> {

  /**
   * 查询用户关联的角色
   *
   * @param userId 用户ID
   * @return 返回关联的角色
   */
  @Select("SELECT r.* FROM sys_user_role AS sur" +
      " LEFT JOIN sys_role AS r ON r.id = sur.role_id WHERE sur.user_id = #{userId}")
  List<SysRoleEntity> findByUser(@Param("userId") String userId);

  /**
   * 统计用户是否拥有角色
   *
   * @param userId 用户ID
   * @param roles  角色ID
   * @return 返回统计的角色的条数
   */
  @Transient
  default long countRoleByUser(String userId, @Nullable List<? extends Serializable> roles) {
    return selectCount(lqw()
        .eq(SysUserRoleEntity::getUserId, userId)
        .in(roles != null && !roles.isEmpty(), SysUserRoleEntity::getRoleId, roles));
  }

  /**
   * 统计角色关联的用户
   *
   * @param roles 角色ID
   * @return 返回统计的角色的条数
   */
  @Transient
  default long countUserByRole(List<? extends Serializable> roles) {
    return selectCount(lqw().in(roles != null && !roles.isEmpty(), SysUserRoleEntity::getRoleId, roles));
  }

  /**
   * 全出全部的
   *
   * @param userId 用户ID
   * @param roles  角色ID
   * @return 返回删除的条数
   */
  default int deleteAll(String userId, List<String> roles) {
    return delete(lqw()
        .eq(SysUserRoleEntity::getUserId, userId)
        .in(SysUserRoleEntity::getRoleId, roles));
  }

}
