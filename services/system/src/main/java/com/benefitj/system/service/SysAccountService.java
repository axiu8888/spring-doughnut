package com.benefitj.system.service;

import com.benefitj.core.HexUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.system.mapper.SysAccountMapper;
import com.benefitj.system.model.SysAccountEntity;
import com.benefitj.scaffold.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class SysAccountService extends BaseService<SysAccountMapper, SysAccountEntity> {

  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private RedisService redisService;

  /**
   * 通过账号名获取
   *
   * @param username 用户名
   * @return 返回查询的账号
   */
  public SysAccountEntity getByUsername(String username) {
    SysAccountEntity account = redisService.getAccount(username);
    if (account != null) {
      return account;
    }
    account = getBaseMapper().selectByUserName(username);
    if (account != null) {
      // 缓存
      redisService.setAccount(account);
    }
    return account;
  }

  public long countByUsername(String username) {
    return getBaseMapper().countByUsername(username);
  }

  /**
   * 创建账号
   *
   * @param account 账号
   */
  @Override
  public boolean save(SysAccountEntity account) {
    if (countByUsername(account.getUsername()) > 0) {
      throw new SysException("此账号已存在");
    }

    account.setId(IdUtils.uuid());
    // 对密码加密
    String hex = HexUtils.bytesToHex(account.getPassword().getBytes());
    account.setPassword(passwordEncoder.encode(hex));
    account.setActive(Boolean.TRUE);
    // 缓存
    redisService.setAccount(account);
    return super.save(account);
  }

  /**
   * 改变账号可用状态
   *
   * @param id     账号ID
   * @param active 状态
   * @return 返回是否更新
   */
  public boolean changeActive(String id, Boolean active) {
    SysAccountEntity account = getById(id);
    if (account != null) {
      account.setActive(active == null ? account.getActive() : active);
      if (Boolean.FALSE.equals(active)) {
        redisService.deleteAccount(account.getUsername());
      }
      return updateById(account);
    }
    return false;
  }

  /**
   * 根据用户ID获取帐号信息
   *
   * @param userId 用户ID
   * @return 返回帐号信息
   */
  public SysAccountEntity getByUserId(String userId) {
    SysAccountEntity account = redisService.getAccountByUserId(userId);
    if (account != null) {
      return account;
    }
    account = getBaseMapper().selectByUserId(userId);
    if (account != null) {
      redisService.setUsername(userId, account.getUsername());
      redisService.setAccount(account);
    }
    return account;
  }

  /**
   * 修改密码
   *
   * @param userId      用户ID
   * @param oldPassword 旧密码
   * @param newPassword 新密码
   * @return 返回是否修改
   */
  public boolean changePassword(String userId, String oldPassword, String newPassword) {
    SysAccountEntity account = getByUserId(userId);
    if (account != null) {
      // 验证账号和密码
      validate(account, oldPassword);
      // 对密码加密
      String hex = HexUtils.bytesToHex(newPassword.getBytes(StandardCharsets.UTF_8));
      account.setPassword(passwordEncoder.encode(hex));
      redisService.deleteAccount(account.getUsername());
      return updateById(account);
    }
    return false;
  }

  public void validate(SysAccountEntity account, String password) {
    String rawPassword = HexUtils.bytesToHex(password.getBytes(StandardCharsets.UTF_8));
    if (account == null || !passwordEncoder.matches(rawPassword, account.getPassword())) {
      throw new SysException("账号或密码错误");
    }

    if (!Boolean.TRUE.equals(account.getActive())) {
      throw new SysException("账号不可用");
    }
  }

}
