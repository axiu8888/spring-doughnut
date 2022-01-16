package com.benefitj.scaffold.security;

import com.benefitj.core.EnumHelper;
import com.benefitj.scaffold.security.token.JwtToken;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.aop.log.HttpLoggingHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Performs validation of provided JWT Token.
 */
public class JwtTokenAuthenticationProcessingFilter extends OncePerRequestFilter {

  private static final String HEADER_NAME = "Authorization";

  /**
   * token manager
   */
  private JwtTokenManager tokenManager;
  /**
   * 请求匹配
   */
  private PathRequestMatcher requestMatcher;
  /**
   * 认证管理
   */
  private AuthenticationManager authenticationManager;
  /**
   * 认证失败的处理器
   */
  private JwtAuthenticationFailureHandler failureHandler;

  public JwtTokenAuthenticationProcessingFilter() {
  }

  public JwtTokenAuthenticationProcessingFilter(JwtTokenManager tokenManager,
                                                PathRequestMatcher requestMatcher,
                                                AuthenticationManager authenticationManager,
                                                JwtAuthenticationFailureHandler failureHandler) {
    this.tokenManager = tokenManager;
    this.requestMatcher = requestMatcher;
    this.authenticationManager = authenticationManager;
    this.failureHandler = failureHandler;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    String rawToken = request.getHeader(HEADER_NAME);
    if (StringUtils.isBlank(rawToken)
        || EnumHelper.nameEquals(HttpMethod.OPTIONS, request.getMethod())
        || !getRequestMatcher().matches(request)) {
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      chain.doFilter(request, response);
      return;
    }

    try {
      // 解析token
      JwtToken token = tokenManager.parse(rawToken, false, false);
      if (token != null) {
        Authentication authenticate = getAuthenticationManager().authenticate(token);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticate);
        SecurityContextHolder.setContext(context);

        // 打印用户和机构
        HttpLoggingHandler.putArg("userId", token.getUserId());
        HttpLoggingHandler.putArg("orgId", token.getOrgId());
      }
      chain.doFilter(request, response);
    } catch (Exception e) {
      BadCredentialsException bce = new BadCredentialsException(e.getMessage());
      getFailureHandler().onAuthenticationFailure(request, response, bce);
    } finally {
      SecurityContextHolder.clearContext();
    }
  }

  public JwtTokenManager getTokenManager() {
    return tokenManager;
  }

  @Lazy
  @Autowired
  public void setTokenManager(JwtTokenManager tokenManager) {
    this.tokenManager = tokenManager;
  }

  public PathRequestMatcher getRequestMatcher() {
    return requestMatcher;
  }

  @Lazy
  @Autowired
  public void setRequestMatcher(PathRequestMatcher requestMatcher) {
    this.requestMatcher = requestMatcher;
  }

  public AuthenticationManager getAuthenticationManager() {
    return authenticationManager;
  }

  @Lazy
  @Autowired
  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  public JwtAuthenticationFailureHandler getFailureHandler() {
    return failureHandler;
  }

  @Lazy
  @Autowired
  public void setFailureHandler(JwtAuthenticationFailureHandler failureHandler) {
    this.failureHandler = failureHandler;
  }

}
