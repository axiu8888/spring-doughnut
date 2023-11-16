package com.benefitj.spring.security.jwt.token;

import com.benefitj.core.functions.WrappedMap;
import com.benefitj.spring.BeanHelper;
import com.benefitj.spring.security.jwt.JwtUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJwt;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.*;

/**
 * 默认的 JwtToken
 *
 * @author DINGXIUAN
 */
public class DefaultJwtToken extends DefaultJwt<Header, Claims> implements JwtToken, WrappedMap<String, Object> {

  public static final String REFRESH = "refresh";
  /**
   * 权限
   */
  public static final String AUTHORITIES = "authorities";
  /**
   * 机构ID
   */
  public static final String ORG_ID = "orgId";

  /**
   * 原始的 token
   */
  private String rawToken;
  /**
   * 用户信息
   */
  private JwtUserDetails userDetails;
  /**
   * 是否已认证
   */
  private boolean authenticated = false;

  public DefaultJwtToken() {
    super(new DefaultHeader(new LinkedHashMap<>()), new DefaultClaims(new LinkedHashMap<>()));
  }

  public DefaultJwtToken(Jwt<Header, Claims> jwt) {
    super(jwt.getHeader(), jwt.getPayload());
  }

  public DefaultJwtToken(String rawToken, Jwt<Header, Claims> jwt) {
    super(jwt.getHeader(), jwt.getPayload());
    this.rawToken = rawToken;
  }

  @Override
  public boolean isRefresh() {
    Boolean refresh = getBody().get(REFRESH, Boolean.class);
    return Boolean.TRUE.equals(refresh);
  }

  @Override
  public void setRefresh(boolean refresh) {
    getBody().put(REFRESH, refresh);
  }

  @Override
  public String getRawToken() {
    return rawToken;
  }

  @Override
  public void setRawToken(String rawToken) {
    this.rawToken = rawToken;
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
  public String getOrgId() {
    return getBody().get(ORG_ID, String.class);
  }

  @Override
  public void setOrgId(String orgId) {
    getBody().put(ORG_ID, orgId);
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
    throw new UnsupportedOperationException();
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
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    this.authenticated = isAuthenticated;
  }

  @Override
  public String getName() {
    return get("principal", String.class);
  }

  @Override
  public boolean implies(Subject subject) {
    return JwtToken.super.implies(subject);
  }

  @Override
  public String getIssuer() {
    return getBody().getIssuer();
  }

  public void setIssuer(String iss) {
    getBody().put(Claims.ISSUER, iss);
  }

  @Override
  public String getSubject() {
    return getBody().getSubject();
  }

  public void setSubject(String sub) {
    getBody().put(Claims.SUBJECT, sub);
  }

  @Override
  public Set<String> getAudience() {
    return getBody().getAudience();
  }

  public void setAudience(String aud) {
    getBody().put(Claims.AUDIENCE, aud);
  }

  @Override
  public Date getExpiration() {
    return getBody().getExpiration();
  }

  public void setExpiration(Date exp) {
    getBody().put(Claims.EXPIRATION, exp);
  }

  @Override
  public Date getNotBefore() {
    return getBody().getNotBefore();
  }

  public void setNotBefore(Date nbf) {
    getBody().put(Claims.NOT_BEFORE, nbf);
  }

  @Override
  public Date getIssuedAt() {
    return getBody().getIssuedAt();
  }

  public void setIssuedAt(Date iat) {
    getBody().put(Claims.ISSUED_AT, iat);
  }

  @Override
  public String getId() {
    return getBody().getId();
  }

  @Override
  public <T> T get(String claimName, Class<T> requiredType) {
    return getBody().get(claimName, requiredType);
  }

  public void setId(String jti) {
    getBody().put(Claims.ID, jti);
  }

  /**
   * 拷贝
   */
  @Override
  public DefaultJwtToken copy() {
    DefaultJwtToken copy = BeanHelper.copy(this, DefaultJwtToken.class);
    copy.getHeader().putAll(getHeader());
    copy.getBody().putAll(getBody());
    return copy;
  }

  @Override
  public Map<String, Object> map() {
    return getBody();
  }
}
