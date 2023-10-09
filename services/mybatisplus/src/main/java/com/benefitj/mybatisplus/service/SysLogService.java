package com.benefitj.mybatisplus.service;

import com.benefitj.mybatisplus.dao.mapper.SysLogMapper;
import com.benefitj.mybatisplus.entity.SysOpLog;
import org.springframework.stereotype.Service;

/**
 * 系统日志
 */
@Service
public class SysLogService extends ServiceBase<SysOpLog, SysLogMapper> {

  public SysLogService(SysLogMapper mapper) {
    super(mapper);
  }

}
