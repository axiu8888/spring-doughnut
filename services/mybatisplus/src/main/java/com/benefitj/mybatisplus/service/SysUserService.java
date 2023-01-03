package com.benefitj.mybatisplus.service;

import com.benefitj.mybatisplus.mapper.SysUerMapper;
import com.benefitj.mybatisplus.model.SysUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserService extends ServiceBase<SysUserEntity, SysUerMapper> {

  @Autowired
  public SysUserService(SysUerMapper mapper) {
    super(mapper);
  }

}
