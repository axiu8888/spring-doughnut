package com.benefitj.system.controller;

import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.query.PageBody;
import com.benefitj.spring.mvc.query.PageRequest;
import com.benefitj.spring.mvc.query.QueryBody;
import com.benefitj.spring.mvc.query.QueryRequest;
import com.benefitj.system.model.SysLogEntity;
import com.benefitj.system.service.SysLogService;
import com.benefitj.system.utils.Utils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色
 */
@AopWebPointCut
@Api(tags = {"操作日志"}, description = "对日志的各种操作")
@RestController
@RequestMapping("/logs")
public class SysLogController {

  @Autowired
  private SysLogService logService;

  @ApiOperation("获取")
  @GetMapping
  public HttpResult<SysLogEntity> get(@ApiParam("日志ID") String id) {
    return HttpResult.succeed(logService.getById(id));
  }

  @ApiOperation("删除")
  @DeleteMapping
  public HttpResult<Integer> delete(@ApiParam("ID") String id) {
    return HttpResult.succeed(logService.deleteById(id));
  }

  @ApiOperation("分页")
  @GetMapping("/page")
  public HttpResult<PageInfo<SysLogEntity>> getPage(@PageBody PageRequest<SysLogEntity> request) {
    Utils.setOrgId(request.getCondition());
    return HttpResult.succeed(logService.getPage(request));
  }

  @ApiOperation("列表")
  @GetMapping("/list")
  public HttpResult<List<SysLogEntity>> getList(@QueryBody QueryRequest<SysLogEntity> request) {
    Utils.setOrgId(request.getCondition());
    return HttpResult.succeed(logService.getList(request));
  }

}
