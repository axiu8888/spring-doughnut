package com.benefitj.system.security;

import com.benefitj.spring.mvc.mapping.ApiDescriptor;
import com.benefitj.spring.mvc.mapping.MappingAnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class RoleResourceAuthorizationHandler implements ResourceAuthorizationHandler {

  @Override
  public boolean isPermitted(ApiDescriptor descriptor, Object[] args) {
    MappingAnnotationMetadata metadata = descriptor.getMetadata();
    if (metadata.isMethodAnnotationPresent(ResourceTag.class)) {
      return true;
    }

    descriptor.getPaths()
        .stream()
        .anyMatch(new Predicate<String>() {
          @Override
          public boolean test(String path) {
            return false;
          }
        });

    return false;
  }

}
