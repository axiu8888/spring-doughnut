package com.benefitj.spring.freemarker;

/**
 * 属性模板
 */
public class PropertyTemplate {

  /**
   * 类型
   */
  private String type;
  /**
   * 名称
   */
  private String name;
  /**
   * 访问修饰符
   */
  private String modifier;
  /**
   * set方法前缀
   */
  private String setterPrefix = "set";
  /**
   * 是否生成Setter
   */
  private boolean generateSetter = true;
  /**
   * get方法前缀
   */
  private String getterPrefix = "get";
  /**
   * 是否生成Getter
   */
  private boolean generateGetter = true;


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getModifier() {
    return modifier;
  }

  public void setModifier(String modifier) {
    this.modifier = modifier;
  }

  public String getSetterPrefix() {
    return setterPrefix;
  }

  public void setSetterPrefix(String setterPrefix) {
    this.setterPrefix = setterPrefix;
  }

  public boolean isGenerateSetter() {
    return generateSetter;
  }

  public void setGenerateSetter(boolean generateSetter) {
    this.generateSetter = generateSetter;
  }

  public String getGetterPrefix() {
    return getterPrefix;
  }

  public void setGetterPrefix(String getterPrefix) {
    this.getterPrefix = getterPrefix;
  }

  public boolean isGenerateGetter() {
    return generateGetter;
  }

  public void setGenerateGetter(boolean generateGetter) {
    this.generateGetter = generateGetter;
  }
}
