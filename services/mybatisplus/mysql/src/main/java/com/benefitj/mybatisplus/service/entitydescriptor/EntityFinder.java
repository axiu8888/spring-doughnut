package com.benefitj.mybatisplus.service.entitydescriptor;

import com.baomidou.mybatisplus.annotation.TableField;
import com.benefitj.core.CamelCaseUtils;
import com.benefitj.core.ReflectUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface EntityFinder {

  /**
   * 获取实体类
   *
   * @param klass         Service或Mapper接口
   * @param typeParamName 泛型参数名称
   * @return 返回获取的实体类
   */
  EntityDescriptor getEntityClass(Class<?> klass, String typeParamName);

  /**
   * 默认实现
   */
  EntityFinder INSTANCE = new SimpleEntityFinder();

  class SimpleEntityFinder implements EntityFinder {

    /**
     * 缓存实体类
     */
    private final Map<Class<?>, EntityDescriptor> entities = new ConcurrentHashMap<>();

    public SimpleEntityFinder() {
    }

    /**
     * 获取实体类类型
     */
    @Override
    public EntityDescriptor getEntityClass(Class<?> klass, String typeParamName) {
      EntityDescriptor entity = entities.get(klass);
      if (entity != null) {
        return entity;
      }
      return entities.computeIfAbsent(klass, type -> parserEntity(klass, typeParamName));
    }

    protected EntityDescriptor parserEntity(Class<?> type, String typeParamName) {
      EntityDescriptor entity = new EntityDescriptor();
      Class<Object> entityType = ReflectUtils.getParameterizedTypeClass(type, typeParamName);
      entity.setEntityType(entityType);

      if (entityType.isAnnotationPresent(Table.class)) {
        entity.setTableName(entityType.getAnnotation(Table.class).name());
      } else if (entityType.isAnnotationPresent(TableField.class)) {
        entity.setTableName(entityType.getAnnotation(TableField.class).value());
      } else {
        entity.setTableName(CamelCaseUtils.camelToUnderLine(entityType.getSimpleName()));
      }

      ReflectUtils.findFields(entityType
          , f -> !(ReflectUtils.isStaticOrFinal(f.getModifiers())
              || f.isAnnotationPresent(Transient.class)
              || Modifier.isTransient(f.getModifiers()))
          , f -> entity.getProperties().add(parseProperty(f))
          , f -> false
      );
      return entity;
    }

    protected PropertyDescriptor parseProperty(Field field) {
      PropertyDescriptor property = new PropertyDescriptor();
      property.setField(field);
      property.setName(field.getName());
      property.setType(field.getType());
      if (field.isAnnotationPresent(Column.class)) {
        property.setColumn(field.getAnnotation(Column.class).name());
      } else if (field.isAnnotationPresent(TableField.class)) {
        property.setColumn(field.getAnnotation(TableField.class).value());
      } else {
        property.setColumn(CamelCaseUtils.camelToUnderLine(field.getName()));
      }
      property.setPrimaryKey(field.isAnnotationPresent(Id.class));
      return property;
    }

  }

}
