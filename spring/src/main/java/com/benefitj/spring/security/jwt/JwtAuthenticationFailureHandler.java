package com.benefitj.spring.security.jwt;

import com.benefitj.spring.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证失败
 */
@Slf4j
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

  public JwtAuthenticationFailureHandler() {
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException e) throws IOException {
    String error = e.getMessage();
    log.info("认证失败: {}", error);
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    ServletUtils.write(response, status.value(), StringUtils.getIfBlank(error, status::getReasonPhrase));
  }

}
