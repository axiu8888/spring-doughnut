package com.benefitj.mybatisplus.controller;

import com.benefitj.core.Utils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.core.Regex;
import com.benefitj.spring.annotation.AnnotationBeanProcessor;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.MetadataHandler;
import com.benefitj.spring.ctx.SpringCtxHolder;
import com.benefitj.spring.mvc.mapping.MappingAnnotationResolver;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RequestMappingSearcher extends AnnotationBeanProcessor implements MetadataHandler {

  static final List<Class<? extends Annotation>> CONTROLLERS = Arrays.asList(Controller.class, RestController.class);

  private final Regex regex = new Regex("\\$\\{(.*?)}");
  /**
   * 映射的请求
   */
  private Map<String, String> mappings = new LinkedHashMap<>();
  /**
   * 扫描的包路径
   */
  private List<String> basePackages = new ArrayList<>();

  public RequestMappingSearcher() {
    setResolver(new MappingAnnotationResolver());
    setMetadataHandler(this);
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (!ReflectUtils.isAnyAnnotationPresent(AopUtils.getTargetClass(bean), CONTROLLERS)) {
      return bean;
    }
    return super.postProcessAfterInitialization(bean, beanName);
  }

//  @Override
//  protected AnnotationMetadata resolveMetadata(Object bean, Method method) {
//    Class<?> targetClass = AopUtils.getTargetClass(bean);
//    MappingAnnotationMetadata metadata = new MappingAnnotationMetadata();
//    metadata.setBean(bean);
//    metadata.setBaseMapping(targetClass.getAnnotation(RequestMapping.class));
//    metadata.setMethod(method);
//    metadata.getAnnotations().addAll((Collection) resolveAnnotations(method));
//    for (Annotation annotation : metadata.getAnnotations()) {
//      mapToApi(metadata.getBaseMapping().value(), annotation)
//          .stream()
//          .distinct()
//          .forEach(kv -> metadata.getApis().put(kv.getKey(), kv.getValue()));
//    }
//    return metadata;
//  }

  @Override
  public void handle(List<AnnotationMetadata> list) {
  }

  public List<String> getBasePackages() {
    return basePackages;
  }

  public Map<String, String> getMappings() {
    return mappings;
  }

  private List<KeyValue<String, String>> mapToApi(String[] baseUrls, Annotation annotation) {
    if (annotation instanceof RequestMapping) {
      return mapToApi(baseUrls, ((RequestMapping) annotation).value(), ((RequestMapping) annotation).method());
    } else if (annotation instanceof GetMapping) {
      return mapToApi(baseUrls, ((GetMapping) annotation).value(), RequestMethod.GET);
    } else if (annotation instanceof PostMapping) {
      return mapToApi(baseUrls, ((PostMapping) annotation).value(), RequestMethod.POST);
    } else if (annotation instanceof PutMapping) {
      return mapToApi(baseUrls, ((PutMapping) annotation).value(), RequestMethod.PUT);
    } else if (annotation instanceof DeleteMapping) {
      return mapToApi(baseUrls, ((DeleteMapping) annotation).value(), RequestMethod.DELETE);
    } else if (annotation instanceof PatchMapping) {
      return mapToApi(baseUrls, ((PatchMapping) annotation).value(), RequestMethod.PATCH);
    }
    return Collections.emptyList();
  }

  private List<KeyValue<String, String>> mapToApi(String[] baseUrls, String[] paths, RequestMethod... methods) {
    return Stream.of(methods)
        .flatMap(method -> Stream.of(paths)
            .map(path -> new KeyValue<>(path, method.name())))
        .flatMap(kv -> Stream.of(baseUrls)
            .map(baseUrl -> {
              List<String> strings = regex.find(baseUrl);
              if (strings.isEmpty()) {
                return baseUrl;
              }
              StringBuilder sb = new StringBuilder();
              int index = 0;
              for (String group : strings) {
                sb.append(baseUrl, 0, index = baseUrl.indexOf(group));
                String key = group.replace("${", "").replace(":}", "").replace("}", "");
                String value = SpringCtxHolder.getEnvProperty(key);
                if (value != null) {
                  sb.append(value);
                }
                index += group.length();
              }
              if (sb.charAt(sb.length() - 1) == '/' && baseUrl.charAt(index) == '/') {
                index++;
              }
              sb.append(baseUrl, Math.min(index, baseUrl.length()), baseUrl.length());
              return sb.toString();
            })
            .map(baseUrl -> Utils.isEndWiths(baseUrl, "/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl)
            .map(baseUrl -> new KeyValue<>(baseUrl + Utils.startWiths(kv.getKey(), "/"), kv.getValue())))
        .collect(Collectors.toList());
  }

  @Builder
  @EqualsAndHashCode(callSuper = true)
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class MappingAnnotationMetadata extends AnnotationMetadata {
    /**
     * 类上的请求
     */
    private RequestMapping baseMapping;
    /**
     * 请求接口
     */
    private final Map<String, String> apis = new LinkedHashMap<>();

  }

  @SuperBuilder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class KeyValue<K, V> {
    K key;
    V value;
  }

}
