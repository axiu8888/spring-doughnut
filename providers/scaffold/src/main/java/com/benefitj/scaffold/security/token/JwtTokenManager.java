package com.benefitj.scaffold.security.token;

import com.alibaba.fastjson.JSON;
import com.benefitj.scaffold.security.user.JwtUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJwt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Factory class that should be always used to create {@link JwtToken}.
 */
public class JwtTokenManager {

  public static final String HEADER_NAME = "Authorization";
  public static final String JWT_PREFIX = "Bearer ";
  private final JwtProperty jwtProperty;

  public JwtTokenManager(JwtProperty jwtProperty) {
    this.jwtProperty = jwtProperty;
  }

  /**
   * 获取当前的 token，如果当前请求未认证，则返回 NULL
   */
  public static JwtToken currentToken() {
    return currentToken(true);
  }

  /**
   * 获取当前的 token，如果当前请求未认证，则返回 NULL
   */
  public static JwtToken currentToken(boolean nullable) throws NullPointerException {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context != null) {
      Authentication authentication = context.getAuthentication();
      if (authentication instanceof JwtToken) {
        return (JwtToken) authentication;
      }
    }
    if (!nullable) {
      throw new NullPointerException("token is null");
    }
    return null;
  }

  /**
   * 获取用户ID，如果当前请求未认证，则返回 NULL
   */
  public static String currentUserId() {
    return checkNotNull(currentToken(), JwtToken::getUserId);
  }

  /**
   * 获取机构ID，如果当前请求未认证，则返回 NULL
   */
  public static String currentOrgId() {
    return checkNotNull(currentToken(), JwtToken::getOrgId);
  }

  /**
   * 获取用户信息详情，如果当前请求未认证，则返回 NULL
   */
  public static JwtUserDetails currentUserDetails() {
    return checkNotNull(currentToken(), JwtToken::getUserDetails);
  }

  /**
   * 生成 JWT token
   *
   * @param expiration 超时时长(分钟)
   * @param secretKey  秘钥
   * @return 返回生成的JWT token
   */
  public static String generate(JwtToken token, Long expiration, String secretKey) {
    return generate(token, expiration, SignatureAlgorithm.HS512, secretKey);
  }

  /**
   * 生成 JWT token
   *
   * @param expiration 超时时长(分钟)
   * @param algo       加密算法
   * @param secretKey  秘钥
   * @return 返回生成的JWT token
   */
  public static String generate(JwtToken token, Long expiration, SignatureAlgorithm algo, String secretKey) {
    LocalDateTime currentTime = LocalDateTime.now();
    JwtBuilder builder = Jwts.builder()
        .setHeader((Map<String, Object>) token.getHeader())
        .setClaims(token.getBody())
        .setId(generateId())
        // 签发时间
        .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()));
    if (expiration != null && !expiration.equals(0L)) {
      builder.setExpiration(Date.from(
          currentTime.plusSeconds(expiration).atZone(ZoneId.systemDefault()).toInstant()));
    }
    return builder.signWith(algo, secretKey).compact();
  }

  /**
   * 创建新的 token
   *
   * @param user       用户信息
   * @param expiration 过期时间(秒)
   * @param issuer     发布者
   * @return 返回新的 token
   */
  public JwtToken newJwtToken(JwtUserDetails user, Integer expiration, String issuer) {
    JwtToken token = createToken();
    token.setUserDetails(user);
    token.setOrgId(user.getOrgId());
    // 设置ID
    token.setId(generateId());
    LocalDateTime currentTime = LocalDateTime.now();
    token.setSubject(user.getUserId());
    token.setIssuer(issuer);
    // 发布时间
    token.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()));
    // 超时时间
    if (expiration != null && expiration.equals(0)) {
      token.setExpiration(Date.from(
          currentTime.plusSeconds(expiration)
              .atZone(ZoneId.systemDefault())
              .toInstant()));
    }
    return token;
  }

  public static String generateId() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  /**
   * 检查不为 null
   *
   * @param t    被检查的对象
   * @param func 处理的函数
   * @return 返回合法的值
   */
  private static <T, R> R checkNotNull(T t, Function<T, R> func) {
    return t != null ? func.apply(t) : null;
  }

  /**
   * Factory method for issuing new JWT Tokens.
   */
  public JwtToken createAccessToken(JwtUserDetails user) {
    JwtProperty property = getJwtProperty();
    JwtToken token = newJwtToken(user, null, property.getIssuer());
    token.setRawToken(generate(token, property.getExpiration(), property.getSigningKey()));
    return token;
  }

  /**
   * 创建 refresh token
   *
   * @param user
   * @return
   */
  public JwtToken createRefreshToken(JwtUserDetails user) {
    JwtProperty property = getJwtProperty();
    JwtToken token = newJwtToken(user, null, property.getIssuer());
    token.setRefresh(true);
    String jwt = generate(token, property.getRefreshExpiration(), property.getSigningKey());
    token.setRawToken(jwt);
    return token;
  }

  public JwtProperty getJwtProperty() {
    return jwtProperty;
  }

  /**
   * 解析 JWT token
   */
  @Nullable
  public JwtToken parse(HttpServletRequest request) {
    return parse(request.getHeader(HEADER_NAME), false, false);
  }

  /**
   * 解析 JWT token
   */
  public JwtToken parse(String token, boolean allowExpired, boolean ignoreRefresh) {
    if (StringUtils.isBlank(token)) {
      return null;
    }
    Jwt<Header, Claims> jwt;
    try {
      token = token.startsWith(JWT_PREFIX) ?
          token.replaceFirst(JWT_PREFIX, "") : token;
      jwt = Jwts.parserBuilder()
          .setSigningKey(getJwtProperty().getSigningKey())
          .build()
          .parse(token);
    } catch (ExpiredJwtException e) {
      if (!allowExpired) {
        throw e;
      }

      // 允许过期
      String[] split = token.split("\\.");
      if (split.length != 3) {
        throw new IllegalStateException("The token is wrong!");
      }
      for (int i = 0; i < 2; i++) {
        split[i] = new String(Base64.getDecoder().decode(split[i]));
      }

      DefaultHeader header = new DefaultHeader<>(JSON.parseObject(split[0]));
      DefaultClaims body = new DefaultClaims(JSON.parseObject(split[1]));
      jwt = new DefaultJwt<>(header, body);
    }

    JwtToken jwtToken = createToken();
    jwtToken.setRawToken(token);
    jwtToken.getHeader().putAll(jwt.getHeader());
    jwtToken.getBody().putAll(jwt.getBody());
    if (!ignoreRefresh && jwtToken.isRefresh()) {
      throw new UnsupportedJwtException("The token is wrong");
    }
    return jwtToken;
  }

  public JwtToken createToken() {
    return new DefaultJwtToken();
  }

}
