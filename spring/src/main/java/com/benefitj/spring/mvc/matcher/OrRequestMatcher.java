package com.benefitj.spring.mvc.matcher;


import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * {@link RequestMatcher} that will return true if any of the passed in
 * {@link RequestMatcher} instances match.
 *
 * @author Rob Winch
 * @since 3.2
 */
public final class OrRequestMatcher implements RequestMatcher {

  private final List<RequestMatcher> requestMatchers;

  /**
   * Creates a new instance
   * @param requestMatchers the {@link RequestMatcher} instances to try
   */
  public OrRequestMatcher(List<RequestMatcher> requestMatchers) {
    Assert.notEmpty(requestMatchers, "requestMatchers must contain a value");
    Assert.isTrue(!requestMatchers.contains(null), "requestMatchers cannot contain null values");
    this.requestMatchers = requestMatchers;
  }

  /**
   * Creates a new instance
   * @param requestMatchers the {@link RequestMatcher} instances to try
   */
  public OrRequestMatcher(RequestMatcher... requestMatchers) {
    this(Arrays.asList(requestMatchers));
  }

  @Override
  public boolean matches(HttpServletRequest request) {
    for (RequestMatcher matcher : this.requestMatchers) {
      if (matcher.matches(request)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "Or " + this.requestMatchers;
  }

}

