package com.benefitj.mybatisplus.dao.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benefitj.mybatisplus.entity.HsReportTaskEntity;
import com.benefitj.mybatisplus.entity.mysql.RecipelItem;
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
public interface MysqlReportTaskMapper extends BaseMapper<HsReportTaskEntity> {

  @Select("<script>\n" +
      "SELECT hrt.orgId, MAX(ho.org_name) AS orgName, MAX(ho.auto_code) AS auto_code, hrt.report_date, COUNT(DISTINCT hrt.zid) AS count\n" +
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
  List<JSONObject> selectByItems(@Param("items") RecipelItem[] items,
                                 @Param("startDate") Date startDate,
                                 @Param("endDate") Date endDate);

}
