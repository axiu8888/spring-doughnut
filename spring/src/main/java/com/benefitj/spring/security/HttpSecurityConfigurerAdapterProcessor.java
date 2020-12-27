package com.benefitj.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class HttpSecurityConfigurerAdapterProcessor {

  private List<HttpSecurityCustomizer> httpSecurityCustomizers;

  protected void process(HttpSecurity http) throws Exception {
    List<HttpSecurityCustomizer> customizers = getHttpSecurityCustomizers();
    if (!CollectionUtils.isEmpty(customizers)) {
      for (HttpSecurityCustomizer customizer : customizers) {
        customizer.customize(http);
      }
    }
  }

  public List<HttpSecurityCustomizer> getHttpSecurityCustomizers() {
    return httpSecurityCustomizers;
  }

  @Autowired(required = false)
  public void setHttpSecurityCustomizers(List<HttpSecurityCustomizer> httpSecurityCustomizers) {
    this.httpSecurityCustomizers = httpSecurityCustomizers;
  }
}
