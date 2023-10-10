package com.benefitj.mybatisplus.log;

import com.benefitj.spring.aop.log.HttpLoggingHandler;
import com.benefitj.spring.security.jwt.token.JwtToken;
import com.benefitj.spring.security.jwt.token.JwtTokenManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 机构过滤器
 */
@Component
public class TokenLogFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    JwtToken token = JwtTokenManager.currentToken();
    try {
      if (token != null) {
        HttpLoggingHandler.putArg("userId", token.getUserId());
        HttpLoggingHandler.putArg("orgId", token.getOrgId());
      }
    } catch (Exception ignore) { /* ~ */ }
     finally {
      chain.doFilter(request, response);
    }
  }
}
