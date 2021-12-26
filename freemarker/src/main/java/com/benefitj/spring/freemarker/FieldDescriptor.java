package com.benefitj.spring.freemarker;

import java.util.List;

/**
 * 属性模板
 */
public class FieldDescriptor {

  /**
   * 访问修饰符
   */
  private ModiferType modifier = ModiferType.PRIVATE;
  /**
   * 类型
   */
  private Class<?> type;
  /**
   * 名称
   */
  private String name;
  /**
   * 描述
   */
  private String description;
  /**
   * 是否生成Setter
   */
  private boolean setter = true;
  /**
   * 是否生成Getter
   */
  private boolean getter = true;

  public ModiferType getModifier() {
    return modifier;
  }

  public FieldDescriptor setModifier(ModiferType modifier) {
    this.modifier = modifier;
    return this;
  }

  public Class<?> getType() {
    return type;
  }

  public FieldDescriptor setType(Class<?> type) {
    this.type = type;
    return this;
  }

  public String getName() {
    return name;
  }

  public FieldDescriptor setName(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public FieldDescriptor setDescription(String description) {
    this.description = description;
    return this;
  }

  public boolean isSetter() {
    return setter;
  }

  public FieldDescriptor setSetter(boolean setter) {
    this.setter = setter;
    return this;
  }

  public boolean isGetter() {
    return getter;
  }

  public FieldDescriptor setGetter(boolean getter) {
    this.getter = getter;
    return this;
  }

  /**
   * 获取Set名称
   */
  public String getSetterName() {
    return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }

  /**
   * 获取Get名称
   */
  public String getGetterName() {
    String prefix = boolean.class == getType() ? "is" : "set";
    return prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }

}
