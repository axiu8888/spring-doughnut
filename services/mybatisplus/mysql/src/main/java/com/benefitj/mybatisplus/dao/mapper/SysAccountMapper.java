package com.benefitj.mybatisplus.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benefitj.mybatisplus.entity.SysAccount;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 账号
 */
@Mapper
public interface SysAccountMapper extends BaseMapper<SysAccount> {

  /**
   * 根据用户ID获取帐号信息
   *
   * @param userId 用户ID
   * @return 返回帐号信息
   */
  default SysAccount selectByUserId(@Param("userId") String userId) {
    return selectOne(new QueryWrapper<SysAccount>()
        .lambda()
        .eq(SysAccount::getUserId, userId)
    );
  }

  /**
   * 通过用户名查询用户
   */
  default SysAccount selectByUserName(@Param("username") String username) {
    return selectOne(new QueryWrapper<SysAccount>()
        .lambda()
        .eq(SysAccount::getUsername, username)
    );
  }

  /**
   * 统计用户名条数
   *
   * @param username 用户名
   * @return 返回统计的条数
   */
  default long countByUsername(@Param("username") String username) {
    return selectCount(new QueryWrapper<SysAccount>()
        .lambda()
        .eq(SysAccount::getUsername, username)
    );
  }

  /**
   * 查询分页数据
   *
   * @param c         条件
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @return 返回查询的数据
   */
  default List<SysAccount> selectList(@Param("c") SysAccount c,
                                      @Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime) {
    return selectList(new QueryWrapper<SysAccount>()
        .lambda()
        .ge(startTime != null, SysAccount::getCreateTime, startTime)
        .le(endTime != null, SysAccount::getCreateTime, endTime)
        .eq(StringUtils.isNotBlank(c.getId()), SysAccount::getId, c.getId())
        .eq(StringUtils.isNotBlank(c.getUserId()), SysAccount::getUserId, c.getUserId())
        .like(StringUtils.isNotBlank(c.getUsername()), SysAccount::getUsername, c.getUsername())
        .eq(StringUtils.isNotBlank(c.getPassword()), SysAccount::getPassword, c.getPassword())
        .eq(c.getLocked() != null, SysAccount::getLocked, c.getLocked())
        .eq(c.getVersion() != null, SysAccount::getVersion, c.getVersion())
    );
  }

}
