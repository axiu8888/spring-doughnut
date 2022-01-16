package com.benefitj.scaffold.security;

import com.benefitj.scaffold.security.user.JwtUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface JwtUserDetailsService extends UserDetailsService {

  /**
   * 获取用户详情
   *
   * @param userId 用户ID
   * @return 返回用户详情
   * @throws UsernameNotFoundException 用户找不到时的异常
   */
  JwtUserDetails getUserDetails(String userId) throws UsernameNotFoundException;

  @Override
  JwtUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
