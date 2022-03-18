package com.benefitj.scaffold.base.entity;

import com.benefitj.core.executable.Instantiator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EntityDescriptor {
  /**
   * 字段
   */
  private final List<PropertyDescriptor> properties = new ArrayList<>();
  /**
   * 实体类型
   */
  private Class<?> entityType;
  /**
   * 表名
   */
  private String tableName;

  /**
   * 获取属性
   *
   * @param filter 过滤器
   * @return 返回匹配的属性
   */
  public PropertyDescriptor getFirst(Predicate<PropertyDescriptor> filter) {
    for (PropertyDescriptor descriptor : properties) {
      if (filter.test(descriptor)) {
        return descriptor;
      }
    }
    return null;
  }

  /**
   * 获取属性
   *
   * @param filter 过滤器
   * @return 返回匹配的属性
   */
  public List<PropertyDescriptor> get(Predicate<PropertyDescriptor> filter) {
    return getProperties()
        .stream()
        .filter(filter)
        .collect(Collectors.toList());
  }

  /**
   * 获取属性
   *
   * @param name 属性名
   * @return 返回匹配的属性
   */
  public PropertyDescriptor get(String name) {
    return getFirst(descriptor -> descriptor.getName().equals(name));
  }

  /**
   * 判断是否有属性
   *
   * @param name 属性名
   * @return 返回判断结果
   */
  public boolean hashProperty(String name) {
    return get(name) != null;
  }

  /**
   * 获取ID列
   */
  public PropertyDescriptor getId() {
    return getFirst(PropertyDescriptor::isPrimaryKey);
  }

  public <T> T newEntity() {
    return (T) Instantiator.INSTANCE.create(getEntityType());
  }
}

