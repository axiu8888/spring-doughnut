package com.benefitj.scaffold.security;

import com.benefitj.scaffold.security.token.JwtToken;
import com.benefitj.scaffold.security.user.JwtUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * An {@link AuthenticationProvider} implementation that will use provided
 * instance of {@link JwtToken} to perform authentication.
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private JwtUserDetailsService userDetailsService;

  public JwtAuthenticationProvider() {
  }

  public JwtAuthenticationProvider(JwtUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtToken token = (JwtToken) authentication;
    JwtUserDetails userDetails = getUserDetailsService().getUserDetails(token.getUserId());
    if (!userDetails.isAccountNonLocked()) {
      throw new LockedException("Account is locked.");
    }
    if (!userDetails.isAccountNonExpired()) {
      throw new AccountExpiredException("Account is expired.");
    }
    if (!userDetails.isCredentialsNonExpired()) {
      throw new CredentialsExpiredException("Credentials is expired.");
    }
    token.setUserDetails(userDetails);
    return token;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return JwtToken.class.isAssignableFrom(authentication);
  }

  public JwtUserDetailsService getUserDetailsService() {
    return userDetailsService;
  }

  @Autowired
  public void setUserDetailsService(JwtUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

}
