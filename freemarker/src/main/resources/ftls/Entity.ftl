<#if copyright??>${copyright}</#if>
<#-- 包名 -->
package ${basePackage}.entity;

<#-- 全类名 -->
<#list getFullNames() as name>
import ${name};
</#list>
<#if lombok>
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
</#if>

<#-- 描述，注释 -->
/**
 * ${description!"generate by freemarker"}
 *
 * @author ${author!""}
 * @since ${getCreateTime()}
 */
<#if lombok>
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
</#if>
<#if annotations??>
<#list annotations as anno>
@${anno.type.getSimpleName()}(<#if anno.value??>${anno.value}</#if>)
</#list>
</#if>
public class ${className}Entity <#if superClass??>extends ${superClass} </#if>{

<#-- 字段 -->
<#list fields as field>
  <#if field.description??>
  /**
   * ${field.description!""}
   */
  </#if>
  <#if field.annotations??>
  <#list field.annotations as anno>
  @${anno.type.getSimpleName()}<#if anno.value??>(${anno.value})</#if>
  </#list>
  </#if>
  <#if field.modifier??>${field.modifier} </#if>${field.type.getSimpleName()} ${field.name};
</#list>

<#--使用lombok，不主动生成setter/getter-->
<#-- getter && setter -->
<#if !lombok>
    <#list fields as field>
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
</#if>
}
