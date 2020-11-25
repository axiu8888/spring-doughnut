package com.benefitj.athenapdf.api;

import com.alibaba.fastjson.JSON;
import com.benefitj.spring.ServletUtils;
import com.benefitj.spring.aop.log.HttpServletRequestLoggingHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class HeaderPrinterFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      Map<String, String> headers = ServletUtils.getHeaderMap(request);
      HttpServletRequestLoggingHandler.putArg("headers", JSON.toJSONString(headers));
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
