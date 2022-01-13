package com.benefitj.spring.annotation;

import java.util.List;

public interface MetadataHandler {

  /**
   * 处理
   *
   * @param metadatas 注解元信息
   */
  void handle(List<AnnotationMetadata> metadatas);

}
