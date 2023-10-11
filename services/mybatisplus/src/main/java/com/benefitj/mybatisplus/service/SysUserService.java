package com.benefitj.mybatisplus.service;

import com.benefitj.mybatisplus.dao.mapper.SysUerMapper;
import com.benefitj.mybatisplus.entity.SysUser;
import com.benefitj.spring.quartz.worker.ArgType;
import com.benefitj.spring.quartz.worker.QuartzWorker;
import com.benefitj.spring.quartz.worker.QuartzWorkerArg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SysUserService extends ServiceBase<SysUser, SysUerMapper> {

  @QuartzWorker(name = "testQuartz", description = "测试quartz注解")
  public void testQuartzJob(@QuartzWorkerArg(description = "测试id", type = ArgType.STRING) String id) {
    log.info("测试 quartz job: {}", id);
  }

}
