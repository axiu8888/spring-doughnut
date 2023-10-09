package com.benefitj.mybatisplus.service;

import com.benefitj.mybatisplus.dao.mapper.SysUerMapper;
import com.benefitj.mybatisplus.entity.SysUser;
import com.benefitj.spring.quartz.job.ArgType;
import com.benefitj.spring.quartz.job.QuartzJob;
import com.benefitj.spring.quartz.job.QuartzJobArg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SysUserService extends ServiceBase<SysUser, SysUerMapper> {

  @Autowired
  public SysUserService(SysUerMapper mapper) {
    super(mapper);
  }

  @QuartzJob(name = "testQuartz", description = "测试quartz注解")
  public void testQuartzJob(@QuartzJobArg(description = "测试id", type = ArgType.STRING) String id) {
    log.info("测试 quartz job: {}", id);
  }

}
