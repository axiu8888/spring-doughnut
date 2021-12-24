package ${package_name};

<#list classes as cls>
import cls;
</#list>

public class ${entity_name}Entity {

    <#list properties as prop>
        prop.modifier prop.cls prop.name;
    </#list>

    <#list properties as prop>
        public prop.cls prop.name;
    </#list>

}
