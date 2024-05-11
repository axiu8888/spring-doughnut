package com.benefitj.mybatisplus.dao.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benefitj.mybatisplus.entity.mysql.HsReportTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;


/**
 * 报告查询
 */
@DS("mysql")
@Mapper
public interface MysqlMapper extends BaseMapper<HsReportTaskEntity> {

  /**
   * 查询经销商和医院
   *
   * @param id 经销商/医院 ID
   * @return 返回查询的机构
   */
  @Select("<script>" +
      "SELECT ho.*\n" +
      "FROM HS_ORG AS ho\n" +
      "WHERE 1=1\n" +
      "\tAND ho.auto_code LIKE CONCAT((SELECT auto_code FROM HS_ORG WHERE zid = #{id}), '%')\n" +
      "\tAND ho.org_type IN ( 'hospital', 'distributor' )\n" +
      "ORDER BY ho.auto_code ASC"
      + "</script>")
  List<JSONObject> selectOrgList(@Param("id") String id);

  /**
   * 按照reportDate、机构ID、报告类型统计报告
   *
   * @param items     报告类型
   * @param startDate 开始的日期
   * @param endDate   结束的日志
   * @return 返回统计的结果
   */
  @Select("<script>\n" +
      "SELECT hrt.org_zid AS orgId, MAX(ho.org_name) AS orgName, MAX(ho.auto_code) AS orgCode, hrt.report_date AS reportDate, COUNT(DISTINCT hrt.zid) AS count\n" +
      "FROM HS_REPORT_TASK AS hrt\n" +
      "\tLEFT JOIN HS_PERSON AS hp ON hp.zid = hrt.person_zid\n" +
      "\tLEFT JOIN HS_INPATIENT AS hi ON hi.person_zid = hp.zid\n" +
      "\tLEFT JOIN HS_ORG AS ho ON ho.zid = hi.org_zid\n" +
      "WHERE 1=1\n" +
      "\tAND hrt.org_zid IS NOT NULL\n" +
      "\tAND ho.org_name IS NOT NULL\n" +
      "\tAND hrt.`status` IN ('FINISH', 'CONFIRM')\n" +
      "<when test=\"items!=null and items.length > 0\">\tAND hrt.type IN<foreach item='item' collection='items' open='(' close=')' separator=','>#{item}</foreach></when>\n" +
      "<when test=\"startDate!=null\">\tAND STR_TO_DATE(hrt.report_date, '%Y-%m-%d') <![CDATA[>=]]> #{startDate} </when>\n" +
      "<when test=\"endDate!=null\">\tAND STR_TO_DATE(hrt.report_date, '%Y-%m-%d') <![CDATA[<]]> #{endDate} </when>\n" +
      "GROUP BY hrt.org_zid, hrt.report_date\n" +
      "ORDER BY hrt.report_date DESC, hrt.org_zid ASC\n" +
      "</script>")
  List<JSONObject> countByItems(@Param("items") String[] items,
                                @Param("startDate") Date startDate,
                                @Param("endDate") Date endDate);

}
