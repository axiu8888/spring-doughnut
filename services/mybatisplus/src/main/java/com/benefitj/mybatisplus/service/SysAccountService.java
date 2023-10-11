package com.benefitj.mybatisplus.service;

import com.benefitj.core.HexUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.mybatisplus.dao.mapper.SysAccountMapper;
import com.benefitj.mybatisplus.entity.SysAccount;
import com.benefitj.mybatisplus.exception.SysException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * 账号
 */
@Service
public class SysAccountService extends ServiceBase<SysAccount, SysAccountMapper> {

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * 通过账号名获取
   *
   * @param username 用户名
   * @return 返回查询的账号
   */
  public SysAccount getByUsername(String username) {
    return getBaseMapper().selectByUserName(username);
  }

  public long countByUsername(String username) {
    return getBaseMapper().countByUsername(username);
  }

  /**
   * 创建账号
   *
   * @param account 账号
   */
  public boolean save(SysAccount account) {
    if (countByUsername(account.getUsername()) > 0) {
      throw new SysException("此账号已存在");
    }

    account.setId(IdUtils.uuid());
    // 对密码加密
    String hex = HexUtils.bytesToHex(account.getPassword().getBytes());
    account.setPassword(passwordEncoder.encode(hex));
    account.setActive(Boolean.TRUE);
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
    SysAccount account = getById(id);
    if (account != null) {
      account.setActive(active != null ? active : account.getActive());
      account.setUpdateTime(new Date());
      return updateById(account);
    }
    return false;
  }

  /**
   * 获取机构的账号
   *
   * @param condition 条件
   * @return 返回账号列表
   */
  public List<SysAccount> getList(SysAccount condition) {
    return getBaseMapper().selectList(condition, null, null);
  }

  /**
   * 根据用户ID获取帐号信息
   *
   * @param userId 用户ID
   * @return 返回帐号信息
   */
  public SysAccount getByUserId(String userId) {
    return getBaseMapper().selectByUserId(userId);
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
    SysAccount account = getByUserId(userId);
    if (account != null) {
      try {
        // 验证账号和密码
        validate(account, oldPassword);
      } catch (SysException e) {
        throw new SysException("旧密码错误");
      }
      // 对密码加密
      String hex = HexUtils.bytesToHex(newPassword.getBytes(StandardCharsets.UTF_8));
      account.setPassword(passwordEncoder.encode(hex));
      return updateById(account);
    }
    return false;
  }

  public void validate(SysAccount account, String password) {
    String rawPassword = HexUtils.bytesToHex(password.getBytes(StandardCharsets.UTF_8));
    if (account == null || !passwordEncoder.matches(rawPassword, account.getPassword())) {
      throw new SysException("账号或密码错误");
    }

    if (!Boolean.TRUE.equals(account.getActive())) {
      throw new SysException("账号不可用");
    }

    if (Boolean.TRUE.equals(account.getLocked())) {
      throw new SysException("账号被锁定");
    }
  }

}
