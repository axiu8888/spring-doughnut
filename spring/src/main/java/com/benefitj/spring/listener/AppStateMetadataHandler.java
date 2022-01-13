package com.benefitj.spring.listener;

import com.benefitj.core.executable.SimpleMethodInvoker;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.MetadataHandler;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * App注解元信息处理
 */
public class AppStateMetadataHandler implements MetadataHandler {

  @Override
  public void handle(List<AnnotationMetadata> metadatas) {
    for (AnnotationMetadata metadata : metadatas) {
      Annotation onAppStart = metadata.getFirstAnnotation(OnAppStart.class);
      if (onAppStart != null) {
        SimpleMethodInvoker invoker = new SimpleMethodInvoker(metadata.getBean(), metadata.getMethod());
        AppStateHook.registerStart(invoker::invoke);
      }
      Annotation onAppStop = metadata.getFirstAnnotation(OnAppStop.class);
      if (onAppStop != null) {
        SimpleMethodInvoker invoker = new SimpleMethodInvoker(metadata.getBean(), metadata.getMethod());
        AppStateHook.registerStop(invoker::invoke);
      }
    }
  }

}
