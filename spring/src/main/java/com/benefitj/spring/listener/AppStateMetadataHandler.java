package com.benefitj.spring.listener;

import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.spring.annotationprcoessor.AnnotationMetadata;
import com.benefitj.spring.annotationprcoessor.MetadataHandler;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * App注解元信息处理
 */
public class AppStateMetadataHandler implements MetadataHandler {

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      Annotation annotation = metadata.getAnnotation();
      final SimpleMethodInvoker invoker = new SimpleMethodInvoker(metadata.getBean(), metadata.getMethod());
      if (annotation instanceof OnAppStart) {
        AppStateHook.registerStart(invoker::invoke);
      } else if (annotation instanceof OnAppStop) {
        AppStateHook.registerStop(invoker::invoke);
      }
    }
  }

}
