package com.benefitj.mybatisplus.dao.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benefitj.mybatisplus.entity.HsReportTaskEntity;
import org.apache.ibatis.annotations.Mapper;


/**
 * 报告查询
 */
@DS("master")
@Mapper
public interface PostgresqlReportTaskMapper extends BaseMapper<HsReportTaskEntity> {
}
