package com.benefitj.scaffold.file;

import com.benefitj.scaffold.security.token.JwtToken;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 设置当前用户文件管理的过滤器
 */
public class FileManagerFilter extends OncePerRequestFilter {

  private SystemFileManager fileManager;

  public FileManagerFilter(SystemFileManager fileManager) {
    this.fileManager = fileManager;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      JwtToken token = JwtTokenManager.currentToken(true);
      if (token != null) {
        fileManager.setCurrentUser(token.getUserId());
      }
      filterChain.doFilter(request, response);
    } finally {
      fileManager.removeUser();
    }
  }

}
