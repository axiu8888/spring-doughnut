package com.benefitj.system.service;

import com.benefitj.system.controller.vo.JwtGrantedAuthority;
import com.benefitj.system.controller.vo.SimpleJwtAccountDetails;
import com.benefitj.system.model.SysAccountEntity;
import com.benefitj.system.model.SysUserEntity;
import com.benefitj.scaffold.http.AuthTokenVo;
import com.benefitj.scaffold.security.JwtUserDetailsService;
import com.benefitj.scaffold.security.token.JwtToken;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.scaffold.security.user.JwtUserDetails;
import com.benefitj.spring.BeanHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户认证(登录，注册)
 */
@Service
public class UserAuthenticationService implements JwtUserDetailsService {

  @Autowired
  private JwtTokenManager jwtTokenManager;

  @Autowired
  private SysAccountService accountService;

  @Autowired
  private SysUserService userService;

  @Autowired
  private SysUserAndRoleService uarService;

  @Override
  public JwtUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    JwtToken token = JwtTokenManager.currentToken(true);
    if (token != null
        && token.getUserDetails().getUsername().equals(username)) {
      return token.getUserDetails();
    }
    SysAccountEntity account = accountService.getByUsername(username);
    if (account == null) {
      throw new UsernameNotFoundException("The username is not found");
    }
    return getUserDetails(account);
  }

  /**
   * 注册
   *
   * @param username 用户名
   * @param password 账号密码
   * @param orgId    机构ID
   * @return 返回创建的信息
   */
  public AuthTokenVo<SysAccountEntity> register(String username, String password, String orgId) {
    if (accountService.countByUsername(username) > 0) {
      throw new SysException("此账号已存在");
    }

    SysUserEntity user = new SysUserEntity();
    user.setOrgId(orgId);
    userService.save(user);

    SysAccountEntity account = new SysAccountEntity();
    account.setUsername(username);
    account.setPassword(password);
    account.setUserId(user.getId());
    // 保存用户
    accountService.save(account);

    JwtUserDetails userDetails = getUserDetails(account);
    JwtToken accessToken = jwtTokenManager.createAccessToken(userDetails);
    JwtToken refreshToken = jwtTokenManager.createRefreshToken(userDetails);

    AuthTokenVo<SysAccountEntity> vo = new AuthTokenVo<>();
    vo.setAccessToken(accessToken.getRawToken());
    vo.setRefreshToken(refreshToken.getRawToken());
    vo.setData(account);
    return vo;
  }

  /**
   * 通过用户名和密码登录
   *
   * @param username 用户名
   * @param password 密码
   * @return 返回登陆后的 token
   */
  public AuthTokenVo login(String username, String password) {
    SysAccountEntity account = accountService.getByUsername(username);
    // 验证密码
    accountService.validate(account, password);

    JwtUserDetails userDetails = getUserDetails(account);

    JwtToken accessJwtToken = jwtTokenManager.createAccessToken(userDetails);
    JwtToken refreshJwtToken = jwtTokenManager.createRefreshToken(userDetails);
    AuthTokenVo vo = new AuthTokenVo();
    vo.setAccessToken(accessJwtToken.getRawToken());
    vo.setRefreshToken(refreshJwtToken.getRawToken());
    return vo;
  }

  /**
   * 通过用户ID获取用户信息详情
   *
   * @param userId 用户ID
   * @return 返回用户详情
   */
  @Override
  public JwtUserDetails getUserDetails(String userId) {
    JwtToken token = JwtTokenManager.currentToken(true);
    if (token != null && token.getUserDetails().getUserId().equals(userId)) {
      return token.getUserDetails();
    }
    SysAccountEntity account = accountService.getByUserId(userId);
    if (account == null) {
      throw new UsernameNotFoundException("The account is not found.");
    }
    return getUserDetails(account);
  }

  /**
   * 获取用户信息详情
   *
   * @param account 用户
   * @return 返回用户详情
   */
  public JwtUserDetails getUserDetails(SysAccountEntity account) {
    // 转换为 UserDetails，并查询用户的角色(权限)
    JwtUserDetails userDetails = BeanHelper.copy(account, SimpleJwtAccountDetails.class);
    List<GrantedAuthority> authorityList = uarService.getRoleByUserId(account.getId())
        .stream()
        // 过滤不可用的角色
        //.filter(r -> Boolean.TRUE.equals(r.getActive()))
        .flatMap(r -> Stream.of(BeanHelper.copy(r, JwtGrantedAuthority.class)))
        .collect(Collectors.toList());
    userDetails.setAuthorities(authorityList);

    SysUserEntity user = userService.getById(account.getUserId());
    userDetails.setOrgId(user.getOrgId());

    return userDetails;
  }

  /**
   * 通过RefreshToken获取新的AccessToken
   *
   * @param refreshToken 刷新的token
   * @return 返回access token
   */
  public AuthTokenVo getAccessToken(String refreshToken) {
    JwtToken jwtToken = jwtTokenManager.parse(refreshToken, false, true);
    if (!jwtToken.isRefresh()) {
      throw new SysException("refreshToken错误");
    }

    JwtUserDetails userDetails = getUserDetails(jwtToken.getUserId());
    JwtToken accessToken = jwtTokenManager.createAccessToken(userDetails);
    AuthTokenVo vo = new AuthTokenVo();
    vo.setAccessToken(accessToken.getRawToken());
    vo.setRefreshToken(refreshToken);
    return vo;
  }

  /**
   * 删除帐号
   *
   * @param id    帐号ID
   * @param force 是否强制删除
   * @return 返回删除的条数
   */
  public int deleteAccount(String id, Boolean force) {
    SysAccountEntity account = accountService.getById(id);
    if (account != null) {
      if (Boolean.TRUE.equals(force)) {
        accountService.getBaseMapper().deleteById(account.getId());
      } else {
        accountService.changeActive(id, false);
      }
      userService.changeActive(id, false);
      return 1;
    }
    return 0;
  }

}
