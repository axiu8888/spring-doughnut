package com.benefitj.spring.security.jwt.token;

import com.benefitj.spring.security.jwt.JwtUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * token
 */
public interface JwtToken extends Jwt<Header, Claims>, Claims, Authentication {

  @Override
  String getIssuer();

  void setIssuer(String iss);

  @Override
  String getSubject();

  void setSubject(String sub);

  @Override
  Set<String> getAudience();

  void setAudience(String aud);

  @Override
  Date getExpiration();

  void setExpiration(Date exp);

  @Override
  Date getNotBefore();

  void setNotBefore(Date nbf);

  @Override
  Date getIssuedAt();

  void setIssuedAt(Date iat);

  @Override
  String getId();

  void setId(String jti);

  /**
   * 是否为 refresh token
   */
  boolean isRefresh();

  /**
   * 设置是否为 refresh token
   *
   * @param refresh
   */
  void setRefresh(boolean refresh);

  /**
   * 获取原始 token
   */
  String getRawToken();

  /**
   * 设置原始 token
   *
   * @param rawToken
   */
  void setRawToken(String rawToken);

  /**
   * 获取用户ID
   */
  String getUserId();

  /**
   * 设置用户ID
   *
   * @param userId 用户ID
   */
  void setUserId(String userId);

  /**
   * 获取机构ID
   */
  String getOrgId();

  /**
   * 设置机构ID
   *
   * @param orgId 机构ID
   */
  void setOrgId(String orgId);

  /**
   * 获取 userDetails
   */
  JwtUserDetails getUserDetails();

  /**
   * 设置 userDetails
   *
   * @param userDetails
   */
  void setUserDetails(JwtUserDetails userDetails);

  /**
   * 获取权限
   */
  @Override
  List<GrantedAuthority> getAuthorities();

  /**
   * 权限
   *
   * @param authorities
   */
  void setAuthorities(List<GrantedAuthority> authorities);

  /**
   * 拷贝
   */
  JwtToken copy();

}
