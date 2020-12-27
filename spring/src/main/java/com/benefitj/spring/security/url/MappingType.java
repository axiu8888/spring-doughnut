package com.benefitj.spring.security.url;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Mapping注解类型
 */
public enum MappingType {
  /**
   * any request
   */
  REQUEST(RequestMapping.class),
  /**
   * GET request
   */
  GET(GetMapping.class),
  /**
   * POST request
   */
  POST(PostMapping.class),
  /**
   * PUT request
   */
  PUT(PutMapping.class),
  /**
   * DELETE request
   */
  DELETE(DeleteMapping.class),
  /**
   * PATCH request
   */
  PATCH(PatchMapping.class),
  // ~
  ;

  private final Class<? extends Annotation> mappingType;

  MappingType(Class<? extends Annotation> mappingType) {
    this.mappingType = mappingType;
  }

  public Class<? extends Annotation> getMappingType() {
    return mappingType;
  }

  public RequestMethod getRequestMethod() {
    if (this == REQUEST) {
      return null;
    }
    return RequestMethod.valueOf(name());
  }

  public HttpMethod getHttpMethod() {
    if (this == REQUEST) {
      return null;
    }
    return HttpMethod.valueOf(name());
  }

  public static boolean match(AnnotatedElement element) {
    return of(element) != null;
  }

  @Nullable
  public static MappingType of(AnnotatedElement element) {
    for (MappingType value : values()) {
      if (element.isAnnotationPresent(value.mappingType)) {
        return value;
      }
    }
    return null;
  }

}
