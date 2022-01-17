package com.benefitj.spring.mvc.mapping;

import com.benefitj.core.DUtils;
import com.benefitj.core.ReflectUtils;
import com.benefitj.core.Regex;
import com.benefitj.spring.annotation.AnnotationMetadata;
import com.benefitj.spring.annotation.AnnotationResolverImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RequestMapping 查找
 */
public class MappingAnnotationResolver extends AnnotationResolverImpl {

  static final List<Class<? extends Annotation>> CONTROLLERS = Arrays.asList(Controller.class, RestController.class);

  public static final List<Class<? extends Annotation>> MAPPINGS = Arrays.asList(
      RequestMapping.class
      , GetMapping.class
      , PostMapping.class
      , PutMapping.class
      , DeleteMapping.class
      , PatchMapping.class
  );

  private final Regex regex = new Regex("\\$\\{(.*?)}");
  /**
   * 扫描的包路径
   */
  private final List<String> basePackages = new LinkedList<>();

  @Autowired
  private Environment environment;

  public MappingAnnotationResolver() {
    this.register(MAPPINGS);
  }

  @Override
  public Collection<? extends AnnotationMetadata> resolve(Object bean, String beanName) {
    Class<?> targetClass = getTargetClass(bean);
    if (!(matchPackage(targetClass)
        && ReflectUtils.isAnyAnnotationPresent(targetClass, CONTROLLERS))) {
      return Collections.emptyList();
    }
    return super.resolve(bean, beanName);
  }

  @Override
  public AnnotationMetadata resolveMetadata(Object bean, Method method) {
    Class<?> targetClass = AopUtils.getTargetClass(bean);
    MappingAnnotationMetadata metadata = new MappingAnnotationMetadata();
    metadata.setBean(bean);
    metadata.setTargetClass(targetClass);
    metadata.setMethod(method);
    metadata.setBaseMapping(AnnotationUtils.getAnnotation(targetClass, RequestMapping.class));
    metadata.setApi(AnnotationUtils.getAnnotation(targetClass, Api.class));
    metadata.getAnnotations().addAll((Collection) resolveAnnotations(method));
    // 解析路径
    metadata.addDescriptors(metadata.getAnnotations()
        .stream()
        .map(annotation -> resolveApi(metadata, metadata.getBaseUrls(), annotation))
        .collect(Collectors.toList()));
    return metadata;
  }

  /**
   * 匹配包名
   *
   * @param targetClass 目标代理类
   * @return 返回匹配结果
   */
  protected boolean matchPackage(Class<?> targetClass) {
    String packageName = targetClass.getPackageName();
    return !(getBasePackages().isEmpty() || getBasePackages()
        .stream()
        .map(Regex::new)
        .allMatch(regex -> regex.getPattern().pattern().equals(packageName) || regex.find(packageName).isEmpty()));
  }

  protected ApiDescriptor resolveApi(MappingAnnotationMetadata metadata, String[] baseUrls, Annotation annotation) {
    if (annotation instanceof RequestMapping) {
      return resolveApi(metadata, baseUrls, ((RequestMapping) annotation).value(), ((RequestMapping) annotation).method());
    } else if (annotation instanceof GetMapping) {
      return resolveApi(metadata, baseUrls, ((GetMapping) annotation).value(), RequestMethod.GET);
    } else if (annotation instanceof PostMapping) {
      return resolveApi(metadata, baseUrls, ((PostMapping) annotation).value(), RequestMethod.POST);
    } else if (annotation instanceof PutMapping) {
      return resolveApi(metadata, baseUrls, ((PutMapping) annotation).value(), RequestMethod.PUT);
    } else if (annotation instanceof DeleteMapping) {
      return resolveApi(metadata, baseUrls, ((DeleteMapping) annotation).value(), RequestMethod.DELETE);
    } else if (annotation instanceof PatchMapping) {
      return resolveApi(metadata, baseUrls, ((PatchMapping) annotation).value(), RequestMethod.PATCH);
    }
    return null;
  }

  protected ApiDescriptor resolveApi(MappingAnnotationMetadata metadata, String[] baseUrls, String[] paths, RequestMethod... httpMethods) {
    ApiDescriptor ad = new ApiDescriptor();
    ad.setApiOperation(metadata.getMethod().getAnnotation(ApiOperation.class));
    ad.setHttpMethods(Stream.of(httpMethods).map(Enum::name).toArray(String[]::new));
    ad.setPaths(Stream.of(baseUrls)
        .map(this::fillVariable)
        .map(baseUrl -> DUtils.isEndWiths(baseUrl, "/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl)
        .flatMap(baseUrl -> Stream.of(paths.length > 0 ? paths : new String[]{""})
            .map(path -> baseUrl + DUtils.startWiths(path, "/")))
        .collect(Collectors.toList()));
    return ad;
  }

  protected String fillVariable(String br) {
    List<String> strings = regex.find(br);
    if (strings.isEmpty()) {
      return br;
    }
    StringBuilder sb = new StringBuilder();
    int index = 0;
    for (String group : strings) {
      sb.append(br, 0, index = br.indexOf(group, index));
      String key = group.replace("${", "").replace(":}", "").replace("}", "");
      String value = environment.getProperty(key);
      if (value != null) {
        sb.append(value);
      }
      index += group.length();
    }
    if (sb.charAt(sb.length() - 1) == '/' && br.charAt(index) == '/') {
      index++;
    }
    sb.append(br, Math.min(index, br.length()), br.length());
    return sb.toString();
  }

  public List<String> getBasePackages() {
    return basePackages;
  }

  public void addBasePackages(String... basePackages) {
    getBasePackages().addAll(Arrays.asList(basePackages));
  }

}
