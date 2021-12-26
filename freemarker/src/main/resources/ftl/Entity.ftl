<#if copyright??>${copyright}</#if>
<#-- 包名 -->
package ${packageName}.entity;

<#-- 全类名 -->
<#list getFullNames() as name>
import ${name};
</#list>

<#-- 描述，注释 -->
/**
 * ${description!"generate by freemarker"}
 *
 * @author ${author!""}
 * @since ${getCreateTime()}
 */
public class ${className}Entity <#if superClass??>extends ${superClass} </#if>{

<#-- 字段 -->
<#list fieldDescriptors as field>
  <#if field.description??>
  /**
   * ${field.description!""}
   */
  </#if>
  <#if field.modifier??>${field.modifier} </#if>${field.type.getSimpleName()} ${field.name};
</#list>
<#-- getter && setter -->
<#list fieldDescriptors as field>
<#if field.getter>

  public ${field.type.getSimpleName()} ${field.getGetterName()}() {
    return ${field.name};
  }
</#if>
<#if field.setter>

  public void ${field.getSetterName()}(${field.type.getSimpleName()} ${field.name}) {
    this.${field.name} = ${field.name};
  }
</#if>
</#list>
}
