package com.benefitj.mybatisplus.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benefitj.mybatisplus.entity.SysOpLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysOpLog> {
}
