package com.benefitj.mybatisplus.service;

import com.benefitj.mybatisplus.mapper.SysLogMapper;
import com.benefitj.mybatisplus.model.SysOpLogEntity;
import org.springframework.stereotype.Service;

/**
 * 系统日志
 */
@Service
public class SysLogService extends ServiceBase<SysOpLogEntity, SysLogMapper> {

  public SysLogService(SysLogMapper mapper) {
    super(mapper);
  }

}
