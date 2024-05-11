package com.benefitj.mybatisplus.dao.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benefitj.mybatisplus.entity.postgresql.ReportTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;


/**
 * 报告查询
 */
@DS("master")
@Mapper
public interface PostgresqlMapper extends BaseMapper<ReportTaskEntity> {

  @Select("<script>" +
      "WITH sdp AS ( SELECT * FROM sys_depart WHERE id = #{id} )\n" +
      "SELECT sd.id, sd.depart_name AS orgName, sd.org_code AS orgCode, sd.org_category AS type\n" +
      "FROM sys_depart AS sd\n" +
      "\tLEFT JOIN sdp ON 1=1\n" +
      "WHERE 1=1\n" +
      "\tAND sd.org_code LIKE concat(sdp.org_code, '%')\n" +
      "\tAND sd.org_category IN ( 'hospital', 'distributor' )\n" +
      "ORDER BY sd.org_code ASC\n"
      + "</script>")
  List<JSONObject> selectOrgList(@Param("id") String id);


  @Select("<script>" +
      "SELECT MAX(hrt.org_id) AS orgId, MAX(sd.depart_name) AS orgName, MAX(sd.org_code) AS orgCode, MAX(hrt.report_date) AS reportDate, COUNT(hrt.id) AS count\n" +
      "FROM hs_report_task AS hrt\n" +
      "\tLEFT JOIN sys_depart AS sd ON sd.id = hrt.org_id\n" +
      "WHERE 1=1\n" +
      "\tAND TO_DATE(hrt.report_date, 'YYYY-MM-DD') >= date('2020-01-01')\n" +
      "\tAND hrt.`status` IN ('FINISH', 'CONFIRM')\n" +
      "<when test=\"items!=null and items.length > 0\">\tAND hrt.type IN<foreach item='item' collection='items' open='(' close=')' separator=','>#{item}</foreach></when>\n" +
      "<when test=\"startDate!=null\">\tAND TO_DATE(hrt.report_date, 'YYYY-MM-DD') <![CDATA[>=]]> #{startDate} </when>\n" +
      "<when test=\"endDate!=null\">\tAND TO_DATE(hrt.report_date, 'YYYY-MM-DD') <![CDATA[<]]> #{endDate} </when>\n" +
      "GROUP BY hrt.org_id, hrt.report_date\n" +
      "ORDER BY hrt.report_date DESC, hrt.org_id ASC"
      + "</script>")
  List<JSONObject> countByItems(@Param("items") String[] items,
                                @Param("startDate") Date startDate,
                                @Param("endDate") Date endDate);

}
