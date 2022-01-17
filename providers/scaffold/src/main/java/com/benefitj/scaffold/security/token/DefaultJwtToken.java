package com.benefitj.scaffold.security.token;

import com.benefitj.scaffold.security.user.JwtUserDetails;
import com.benefitj.spring.BeanHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJwt;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

/**
 * 默认的 JwtToken
 *
 * @author DINGXIUAN
 */
public class DefaultJwtToken extends DefaultJwt<Claims> implements JwtToken {
  /**
   * 用户信息
   */
  private JwtUserDetails userDetails;

  public DefaultJwtToken() {
    super(new DefaultHeader(), new DefaultClaims());
  }

  public DefaultJwtToken(Jwt<Header, Claims> jwt) {
    super(jwt.getHeader(), jwt.getBody());
  }

  public DefaultJwtToken(String rawToken, Jwt<Header, Claims> jwt) {
    super(jwt.getHeader(), jwt.getBody());
    this.setRawToken(rawToken);
  }

  @Override
  public String getUserId() {
    return getSubject();
  }

  @Override
  public void setUserId(String userId) {
    setSubject(userId);
  }

  @Override
  public JwtUserDetails getUserDetails() {
    return userDetails;
  }

  @Override
  public void setUserDetails(JwtUserDetails userDetails) {
    this.userDetails = userDetails;
  }

  @Override
  public List<GrantedAuthority> getAuthorities() {
    JwtUserDetails userDetails = getUserDetails();
    return userDetails != null ? userDetails.getAuthorities() : Collections.emptyList();
  }

  @Override
  public void setAuthorities(List<GrantedAuthority> authorities) {
    //this.authorities = authorities;
  }

  @Override
  public Object getCredentials() {
    return getRawToken();
  }

  @Override
  public Object getDetails() {
    return getUserDetails();
  }

  @Override
  public Object getPrincipal() {
    return getRawToken();
  }

  @Override
  public String getName() {
    return get("name", String.class);
  }

  /**
   * 拷贝
   */
  @Override
  public DefaultJwtToken copy() {
    DefaultJwtToken copy = new DefaultJwtToken(getRawToken(), this);
    copy.setUserDetails(getUserDetails());
    return copy;
  }

}
