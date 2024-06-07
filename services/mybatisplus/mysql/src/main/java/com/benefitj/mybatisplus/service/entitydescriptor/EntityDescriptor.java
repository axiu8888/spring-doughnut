package com.benefitj.mybatisplus.service.entitydescriptor;

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
   * 主键列
   */
  private final List<PropertyDescriptor> primaryKeys = new ArrayList<>();

  /**
   * 获取属性
   *
   * @param filter 过滤器
   * @return 返回匹配的属性
   */
  public PropertyDescriptor get(Predicate<PropertyDescriptor> filter) {
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
   * @param name 属性名
   * @return 返回匹配的属性
   */
  public PropertyDescriptor get(String name) {
    return get(descriptor -> descriptor.getName().equals(name));
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
    if (getPrimaryKeys().size() != 1) {
      String pkColumns = getPrimaryKeys()
          .stream()
          .map(PropertyDescriptor::getName)
          .collect(Collectors.joining(", "));
      throw new IllegalStateException("主键列不唯一： " + pkColumns);
    }
    return getPrimaryKeys().get(0);
  }

  public <T> T newEntity() {
    return Instantiator.get().create((Class<T>) getEntityType());
  }
}

