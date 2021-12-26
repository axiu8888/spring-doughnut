<#if copyright??>${copyright}</#if>
<#-- 包名 -->
package ${packageName}.mapper;

import com.benefitj.scaffold.mapper.SuperMapper;
import ${packageName}.entity.${className}Entity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.util.Sqls;

import java.util.Date;
import java.util.List;

<#-- 描述，注释 -->
/**
 * ${description!"generate by freemarker"}
 *
 * @author ${author!""}
 * @since ${getCreateTime()}
 */
@Mapper
public interface ${className}Mapper extends SuperMapper<${className}Entity> {

}
