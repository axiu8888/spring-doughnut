package com.benefitj.spring.swagger;

import springfox.documentation.spi.DocumentationType;

/**
 * 文档的类型
 */
public enum SwaggerDocType {

  SWAGGER_12(DocumentationType.SWAGGER_12),
  SWAGGER_2(DocumentationType.SWAGGER_2),
  OAS_30(DocumentationType.OAS_30);

  private final DocumentationType type;

  SwaggerDocType(DocumentationType type) {
    this.type = type;
  }

  public DocumentationType getType() {
    return type;
  }

}
