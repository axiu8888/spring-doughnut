package com.benefitj.spring.security.url;

import com.benefitj.spring.security.HttpSecurityCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 自定义的处理实现
 */
public class UrlRegistryHttpSecurityCustomizer implements HttpSecurityCustomizer {

  private List<UrlRegistryConfigurerCustomizer> urlRegistryConfigurerCustomizer;

  @Override
  public void customize(HttpSecurity security) throws Exception {
    List<UrlRegistryConfigurerCustomizer> customizers = getUrlRegistryConfigurerCustomizer();
    if (!CollectionUtils.isEmpty(customizers)) {
      for (UrlRegistryConfigurerCustomizer customizer : customizers) {
        security.authorizeRequests(customizer);
      }
    }
  }

  public List<UrlRegistryConfigurerCustomizer> getUrlRegistryConfigurerCustomizer() {
    return urlRegistryConfigurerCustomizer;
  }

  @Lazy
  @Autowired(required = false)
  public void setUrlRegistryConfigurerCustomizer(List<UrlRegistryConfigurerCustomizer> urlRegistryConfigurerCustomizer) {
    this.urlRegistryConfigurerCustomizer = urlRegistryConfigurerCustomizer;
  }


}
