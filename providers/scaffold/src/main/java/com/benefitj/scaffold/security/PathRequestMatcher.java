package com.benefitj.scaffold.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * PathRequestMatcher
 */
public class PathRequestMatcher implements RequestMatcher {

  private OrRequestMatcher skipMatchers;
  private OrRequestMatcher processingMatchers;

  public PathRequestMatcher(Map<String, HttpMethod> skipPaths,
                            Map<String, HttpMethod> processingPaths) {
    skipMatchers = create(skipPaths);
    processingMatchers = create(processingPaths.isEmpty()
        ? Collections.singletonMap("/**", null) : processingPaths);
  }

  /**
   * 创建匹配的 OrRequestMatcher
   *
   * @param paths 路径
   * @return 返回 OrRequestMatcher
   */
  public static OrRequestMatcher create(Map<String, HttpMethod> paths) {
    final List<RequestMatcher> matcherList = new LinkedList<>();
    paths.forEach((path, method) ->
        matcherList.add(new AntPathRequestMatcher(path, method != null ? method.name() : null)));
    return new OrRequestMatcher(matcherList);
  }

  @Override
  public boolean matches(HttpServletRequest request) {
    if (skipMatchers.matches(request)) {
      return false;
    }
    return processingMatchers.matches(request);
  }

}
