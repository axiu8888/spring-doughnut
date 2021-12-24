package com.benefitj.spring.freemarker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassTemplate {

  /**
   * 包名
   */
  private String packageName;
  /**
   * 字段模板
   */
  private List<PropertyTemplate> propertyTemplates = new ArrayList<>();

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public List<PropertyTemplate> getPropertyTemplates() {
    return propertyTemplates;
  }

  public void setPropertyTemplates(List<PropertyTemplate> propertyTemplates) {
    this.propertyTemplates = propertyTemplates;
  }

  public List<String> getFullNames() {
    return getPropertyTemplates()
        .stream()
        .map(PropertyTemplate::getType)
        .distinct()
        .sorted()
        .collect(Collectors.toList());
  }

}
