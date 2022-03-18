package com.benefitj.scaffold.security;

import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.spring.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
    log.info("认证失败: {}", e.getMessage());
    respTo(response
        , JsonUtils.toJson(HttpResult.fail(400, StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "Unauthorized"))
        , HttpStatus.OK.value()
    );
  }

  public static void respTo(HttpServletResponse response, String message, int status) {
    try {
      response.setStatus(status);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write(message);
      response.getWriter().flush();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

}
