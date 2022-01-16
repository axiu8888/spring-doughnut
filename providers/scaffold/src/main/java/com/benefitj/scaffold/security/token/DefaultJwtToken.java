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
    super(new DefaultHeader(), new DefaultClaims());
  }

  public DefaultJwtToken(Jwt<Header, Claims> jwt) {
    super(jwt.getHeader(), jwt.getBody());
  }

  public DefaultJwtToken(String rawToken, Jwt<Header, Claims> jwt) {
    super(jwt.getHeader(), jwt.getBody());
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
    return null;
  }

  @Override
  public String getIssuer() {
    return getBody().getIssuer();
  }

  @Override
  public Claims setIssuer(String iss) {
    return getBody().setIssuer(iss);
  }

  @Override
  public String getSubject() {
    return getBody().getSubject();
  }

  @Override
  public Claims setSubject(String sub) {
    return getBody().setSubject(sub);
  }

  @Override
  public String getAudience() {
    return getBody().getAudience();
  }

  @Override
  public Claims setAudience(String aud) {
    return getBody().setAudience(aud);
  }

  @Override
  public Date getExpiration() {
    return getBody().getExpiration();
  }

  @Override
  public Claims setExpiration(Date exp) {
    return getBody().setExpiration(exp);
  }

  @Override
  public Date getNotBefore() {
    return getBody().getNotBefore();
  }

  @Override
  public Claims setNotBefore(Date nbf) {
    return getBody().setNotBefore(nbf);
  }

  @Override
  public Date getIssuedAt() {
    return getBody().getIssuedAt();
  }

  @Override
  public Claims setIssuedAt(Date iat) {
    return getBody().setIssuedAt(iat);
  }

  @Override
  public String getId() {
    return getBody().getId();
  }

  @Override
  public Claims setId(String jti) {
    return getBody().setId(jti);
  }

  @Override
  public <T> T get(String claimName, Class<T> requiredType) {
    return getBody().get(claimName, requiredType);
  }

  @Override
  public int size() {
    return getBody().size();
  }

  @Override
  public boolean isEmpty() {
    return getBody().isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return getBody().containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return getBody().containsValue(value);
  }

  @Override
  public Object get(Object key) {
    return getBody().get(key);
  }

  @Override
  public Object remove(Object key) {
    return getBody().remove(key);
  }

  @Override
  public void putAll(Map m) {
    getBody().putAll(m);
  }

  @Override
  public void clear() {
    getBody().clear();
  }

  @Override
  public Set<String> keySet() {
    return getBody().keySet();
  }

  @Override
  public Collection<Object> values() {
    return getBody().values();
  }

  @Override
  public Set<Map.Entry<String, Object>> entrySet() {
    return getBody().entrySet();
  }

  @Override
  public Object put(String key, Object value) {
    return getBody().put(key, value);
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

}
