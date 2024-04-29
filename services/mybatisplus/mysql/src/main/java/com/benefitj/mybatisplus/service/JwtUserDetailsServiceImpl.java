package com.benefitj.mybatisplus.service;

import com.benefitj.mybatisplus.controller.vo.UserDetailsVo;
import com.benefitj.mybatisplus.entity.SysAccount;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.security.jwt.JwtUserDetails;
import com.benefitj.spring.security.jwt.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceImpl implements JwtUserDetailsService {

  @Autowired
  SysAccountService accountService;

  @Autowired
  SysUserService userService;

  @Override
  public JwtUserDetails getUserDetails(String userId) throws UsernameNotFoundException {
    return loadUserDetails(accountService.getByUserId(userId), userId);
  }

  @Override
  public JwtUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return loadUserDetails(accountService.getByUsername(username), username);
  }

  private JwtUserDetails loadUserDetails(SysAccount account, String key) {
    if (account == null) {
      throw new UsernameNotFoundException("[" + key + "]用户不存在!");
    }
    //SysUser user = userService.getById(userId);
    UserDetailsVo vo = BeanHelper.copy(account, UserDetailsVo.class);
    //vo.setAuthorities(); // 需要查询权限
    vo.setOrgId(vo.getOrgId());
    return vo;
  }

}
