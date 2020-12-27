package com.benefitj.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 抽象的 WebSecurityConfigurerAdapter
 */
public class AbstractWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

  @Autowired
  private HttpSecurityConfigurerAdapterProcessor processor;

  @Override
  protected final void configure(HttpSecurity http) throws Exception {
    processor.process(http);
    configure0(http);
  }

  protected void configure0(HttpSecurity http) throws Exception {
    super.configure(http);
  }

}
