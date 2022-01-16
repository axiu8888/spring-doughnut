package com.benefitj.system.model;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * 资源类型
 */
public enum ResourceType {

  /**
   * 菜单 1
   */
  MENU(1, "menu"),
  /**
   * 页面 2
   */
  PAGE(1 << 1, "page"),
  /**
   * 功能操作 4
   */
  FUNCTIONAL(1 << 2, "functional"),
  /**
   * 文件 8
   */
  FILE(1 << 3, "file");


  private final int value;
  private final String name;

  ResourceType(int value, String name) {
    this.value = value;
    this.name = name;
  }

  /**
   * 迭代并获取资源类型
   *
   * @param func 迭代函数
   * @return 返回对应的资源类型
   */
  @Nullable
  public static <T> T foreach(Function<ResourceType, T> func) {
    return foreach(func, null);
  }

  /**
   * 迭代并获取资源类型
   *
   * @param func         迭代函数
   * @param defaultValue 默认值
   * @return 返回对应的资源类型
   */
  public static <T> T foreach(Function<ResourceType, T> func, T defaultValue) {
    T t;
    for (ResourceType pt : values()) {
      if ((t = func.apply(pt)) != null) {
        return t;
      }
    }
    return defaultValue;
  }

  /**
   * 获取资源类型
   *
   * @param type 类型
   * @return 返回对应的资源类型
   */
  @Nullable
  public static ResourceType of(int type) {
    return foreach(rt -> (rt.value == type) ? rt : null);
  }

  /**
   * 获取资源类型
   *
   * @param name 类型名称
   * @return 返回对应的资源类型
   */
  @Nullable
  public static ResourceType of(String name) {
    if (name != null && !name.isEmpty()) {
      return foreach(rt -> rt.name.equalsIgnoreCase(name) ? rt : null);
    }
    return null;
  }

  /**
   * 判断是否有资源类型匹配
   *
   * @param value 类型值
   * @param types 类型
   * @return 返回是否匹配
   */
  public static boolean anyMatches(int value, ResourceType... types) {
    for (ResourceType rt : types) {
      if ((rt.value & value) == rt.value) {
        return true;
      }
    }
    return false;
  }

  /**
   * 判断是否资源类型全部匹配
   *
   * @param value 类型值
   * @param types 类型
   * @return 返回是否全部匹配
   */
  public static boolean allMatches(int value, ResourceType... types) {
    for (ResourceType rt : types) {
      if ((rt.value & value) != rt.value) {
        return false;
      }
    }
    return true;
  }

  public int getValue() {
    return value;
  }

  public String getName() {
    return name;
  }

}
