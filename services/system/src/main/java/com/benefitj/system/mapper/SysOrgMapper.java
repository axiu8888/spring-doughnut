package com.benefitj.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.benefitj.system.model.SysOrgEntity;
import com.benefitj.scaffold.base.BaseEntity;
import com.benefitj.scaffold.base.SuperMapper;
import com.benefitj.scaffold.base.entitydescriptor.EntityDescriptor;
import com.benefitj.scaffold.base.entitydescriptor.EntityFinder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Date;
import java.util.List;


/**
 * 机构
 */
@Mapper
public interface SysOrgMapper extends SuperMapper<SysOrgEntity> {


  @Override
  default QueryWrapper<SysOrgEntity> qw(SysOrgEntity condition) {
    QueryWrapper<SysOrgEntity> wrapper = new QueryWrapper<>(condition);
    EntityDescriptor ed = getEntityDescriptor();
    ed.get(pd -> pd.getField().getDeclaringClass() != BaseEntity.class
            && !pd.getName().equals("id")
            && !pd.getName().equals("autoCode")
        )
        .forEach(pd -> {
          Object v = pd.getFieldValue(condition);
          wrapper.eq(v != null, pd.getColumn(), v);
        });
    wrapper.and(subWrapper ->
        subWrapper.lambda()
            .eq(SysOrgEntity::getId, condition.getId())
            .or()
            .likeRight(SysOrgEntity::getAutoCode, condition.getAutoCode())
    );
    return wrapper;
  }

  /**
   * 通过机构ID查询 autoCode
   *
   * @param id 机构ID
   * @return 返回查询的 autoCode
   */
  @Select("SELECT auto_code FROM sys_org WHERE id = #{id}")
  String selectAutoCodeById(@Param("id") String id);

  /**
   * 使用正则表达式匹配符合的AutoCode
   *
   * @param c         条件
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @param n         至少匹配的次数(0 ~ ∞)
   * @param m         至多匹配的次数
   * @return 返回符合的机构
   */
  @SelectProvider(type = Provider.class, method = "selectByAutoCodeRegex")
  List<SysOrgEntity> selectByAutoCodeRegex(@Param("c") SysOrgEntity c,
                                           @Param("startTime") Date startTime,
                                           @Param("endTime") Date endTime,
                                           @Param("n") int n,
                                           @Param("m") int m);


  final class Provider {

    /**
     * 通过正则表达式查询数据
     *
     * @param c         条件
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param n         至少匹配的次数(0 ~ ∞)
     * @param m         至多匹配的次数
     * @return 返回查询的数据
     */
    public String selectByAutoCodeRegex(@Param("c") SysOrgEntity c,
                                        @Param("startTime") Date startTime,
                                        @Param("endTime") Date endTime,
                                        @Param("n") int n,
                                        @Param("m") int m) {
      // "SELECT * FROM HS_ORG WHERE auto_code REGEXP '^(" + autoCode + "){1}(:[\\\\w-]+){" + n + "," + m + "}$'"
      EntityDescriptor ed = EntityFinder.INSTANCE.find(c.getClass());
      QueryWrapper<SysOrgEntity> wrapper = new QueryWrapper<>(c);
      ed.get(pd -> pd.getField().getDeclaringClass() != BaseEntity.class && !pd.getName().equals("autoCode"))
          .forEach(pd -> {
            Object v = pd.getFieldValue(c);
            wrapper.eq(v != null, pd.getColumn(), v);
          });
      wrapper.and(StringUtils.isNotBlank(c.getAutoCode()), sqw
          -> sqw.apply("t.auto_code REGEXP \"^(" + c.getAutoCode() + "){1}(:[\\\\w-]+){" + n + "," + m + "}$\""));
      wrapper.orderBy(false, false, LambdaUtils.extract(BaseEntity::getCreateTime).getImplMethodName());
      return wrapper.getSqlSelect();
    }
  }

}
