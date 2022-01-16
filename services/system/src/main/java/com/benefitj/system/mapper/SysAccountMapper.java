package com.benefitj.system.mapper;

import com.benefitj.system.model.SysAccountEntity;
import com.benefitj.scaffold.base.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 账号
 */
@Mapper
public interface SysAccountMapper extends SuperMapper<SysAccountEntity> {

  /**
   * 通过用户名查询用户
   */
  default SysAccountEntity selectByUserName(@Param("username") String username) {
    return selectOne(lqw().eq(SysAccountEntity::getUsername, username));
  }

  /**
   * 统计用户名条数
   *
   * @param username 用户名
   * @return 返回统计的条数
   */
  default long countByUsername(@Param("username") String username) {
    return selectCount(lqw().eq(SysAccountEntity::getUsername, username));
  }

  /**
   * 根据用户ID获取帐号信息
   *
   * @param userId 用户ID
   * @return 返回帐号信息
   */
  default SysAccountEntity selectByUserId(@Param("userId") String userId) {
    return selectOne(lqw().eq(SysAccountEntity::getUserId, userId));
  }

}
