package com.benefitj.system.service;

import com.benefitj.core.IdUtils;
import com.benefitj.system.mapper.SysUerMapper;
import com.benefitj.system.model.SysUserEntity;
import com.benefitj.scaffold.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SysUserService extends BaseService<SysUerMapper, SysUserEntity> {

  @Autowired
  private RedisService redisService;

  /**
   * 获取用户信息
   *
   * @param id 用户ID
   * @return 返回用户信息
   */
  public SysUserEntity get(String id) {
    SysUserEntity user = redisService.getUser(id);
    if (user != null) {
      return user;
    }
    user = getBaseMapper().selectById(id);
    if (user != null) {
      redisService.setUser(user);
    }
    return user;
  }

  /**
   * 保存用户
   *
   * @param user 用户
   * @return 返回保存的信息
   */
  @Override
  public boolean save(SysUserEntity user) {
    if (StringUtils.isNotBlank(user.getId())) {
      user.setUpdateTime(new Date());
      // 缓存
      redisService.setUser(user);
      return updateById(user);
    } else {
      user.setId(IdUtils.uuid());
      user.setCreateTime(new Date());
      user.setActive(Boolean.TRUE);
      // 缓存
      redisService.setUser(user);
      return getBaseMapper().insert(user) > 0;
    }
  }

  /**
   * 改变用户可用状态
   *
   * @param id     用户ID
   * @param active 状态
   * @return 返回是否更新
   */
  public boolean changeActive(String id, Boolean active) {
    SysUserEntity user = get(id);
    if (user != null) {
      user.setActive(active);
      if (Boolean.FALSE.equals(active)) {
        redisService.deleteUser(id);
      }
      return updateById(user);
    }
    return false;
  }

}
