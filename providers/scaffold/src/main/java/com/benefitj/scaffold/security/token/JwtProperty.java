package com.benefitj.scaffold.security.token;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.util.Base64;

@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtProperty {

  private static final SecretKey KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
  private static final String SIGNING_KEY;

  /**
   * 访问的token超时时间，30分钟
   */
  public static final long ACCESS_EXPIRATION = 1800L;
  /**
   * 刷新token的超时时间，7天
   */
  public static final long REFRESH_EXPIRATION = 604800L;

  static {
    SIGNING_KEY = Base64.getEncoder().encodeToString(KEY.getEncoded());
  }

  /**
   * token的过期时间，默认30分钟
   */
  private Long expiration = ACCESS_EXPIRATION;
  /**
   * 刷新token的过期时间，默认是7天
   */
  private Long refreshExpiration = REFRESH_EXPIRATION;
  /**
   * 签发者
   */
  private String issuer;
  /**
   * 签名的KEY
   */
  private String signingKey = SIGNING_KEY;

  public Long getExpiration() {
    return expiration;
  }

  public void setExpiration(Long expiration) {
    this.expiration = expiration;
  }

  public Long getRefreshExpiration() {
    return refreshExpiration;
  }

  public void setRefreshExpiration(Long refreshExpiration) {
    this.refreshExpiration = refreshExpiration;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getSigningKey() {
    return signingKey;
  }

  public void setSigningKey(String signingKey) {
    this.signingKey = signingKey;
  }
}
