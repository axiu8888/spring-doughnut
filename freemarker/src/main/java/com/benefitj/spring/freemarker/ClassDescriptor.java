package com.benefitj.spring.freemarker;

import com.benefitj.core.DateFmtter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 类描述
 *
 * @author dingxiuan
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassDescriptor {

  /**
   * 版权声明
   */
  private String copyright;
  /**
   * 描述
   */
  private String description;
  /**
   * 作者
   */
  private String author;
  /**
   * 包名
   */
  private String basePackage;
  /**
   * 类名
   */
  private String className;
  /**
   * 父类
   */
  private Class<?> superClass;
  /**
   * 接口
   */
  private List<Class<?>> interfaces;
  /**
   * 类上的注解
   */
  private List<AnnotationDescriptor> annotations;
  /**
   * 字段
   */
  private List<FieldDescriptor> fields;
  /**
   * 是否使用lombok
   */
  @Builder.Default
  private boolean lombok = true;

  /**
   * 获取全类名
   */
  public List<String> getFullNames() {
    List<String> fullNames = getFields()
        .stream()
        .flatMap(fd -> {
          if (fd.getAnnotations() != null) {
            return Stream.concat(Stream.of(fd.getType())
                , ofStream(fd.getAnnotations())
                    .flatMap(ad -> Stream.concat(Stream.of(ad.getType()), ofStream(ad.getImports())))
            );
          }
          return Stream.of(fd.getType());
        })
        .filter(type -> !type.getPackageName().equals("java.lang"))
        .map(Class::getName)
        .distinct()
        .sorted()
        .collect(Collectors.toList());
    if (getSuperClass() != null && !fullNames.contains(getSuperClass().getName())) {
      fullNames.add(getSuperClass().getName());
    }
    return fullNames;
  }

  /**
   * 创建时间
   */
  public String getCreateTime() {
    return DateFmtter.fmtNow("yyyy-MM-dd HH:mm:ss");
  }

  static <T> Stream<T> ofStream(Collection<T> c) {
    return c != null ? c.stream() : Stream.empty();
  }

}
