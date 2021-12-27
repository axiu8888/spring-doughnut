package com.benefitj.spring.freemarker;

import com.benefitj.core.DateFmtter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 类描述
 *
 * @author dingxiuan
 */
public class ClassDescriptor {

  /**
   * 版权声明
   */
  private String copyright;
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
  private Class<?> superClass ;
  /**
   * 描述
   */
  private String description = "";
  /**
   * 作者
   */
  private String author = "";
  /**
   * 字段模板
   */
  private List<FieldDescriptor> fieldDescriptors = new ArrayList<>();

  public ClassDescriptor() {
  }

  public String getCopyright() {
    return copyright;
  }

  public ClassDescriptor setCopyright(String copyright) {
    this.copyright = copyright;
    return this;
  }

  public String getBasePackage() {
    return basePackage;
  }

  public ClassDescriptor setBasePackage(String basePackage) {
    this.basePackage = basePackage;
    return this;
  }

  public String getClassName() {
    return className;
  }

  public ClassDescriptor setClassName(String className) {
    this.className = className;
    return this;
  }

  public Class<?> getSuperClass() {
    return superClass;
  }

  public ClassDescriptor setSuperClass(Class<?> superClass) {
    this.superClass = superClass;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public ClassDescriptor setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getAuthor() {
    return author;
  }

  public ClassDescriptor setAuthor(String author) {
    this.author = author;
    return this;
  }

  public List<FieldDescriptor> getFieldDescriptors() {
    return fieldDescriptors;
  }

  public ClassDescriptor setFieldDescriptors(List<FieldDescriptor> fieldDescriptors) {
    this.fieldDescriptors = fieldDescriptors;
    return this;
  }

  /**
   * 获取全类名
   */
  public List<String> getFullNames() {
    List<String> fullNames = getFieldDescriptors()
        .stream()
        .map(FieldDescriptor::getType)
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

}
