package com.benefitj.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.benefitj.mybatisplus.model.SysOpLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统日志
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysOpLogEntity> {
}
