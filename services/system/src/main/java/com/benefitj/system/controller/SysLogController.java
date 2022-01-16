package com.benefitj.system.controller;

import com.benefitj.system.model.SysLogEntity;
import com.benefitj.system.service.SysLogService;
import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.get.GetBody;
import com.benefitj.spring.mvc.page.PageBody;
import com.benefitj.spring.mvc.page.PageableRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

  @ApiOperation("获取操作日志")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "日志ID", required = true, dataType = "String", dataTypeClass = String.class),
  })
  @GetMapping
  public HttpResult<?> get(String id) {
    return HttpResult.succeed(logService.getById(id));
  }

  @ApiOperation("删除操作日志")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "日志ID", dataType = "String"),
  })
  @DeleteMapping
  public HttpResult<?> delete(String id) {
    return HttpResult.succeed(logService.deleteById(id));
  }

  @ApiOperation("获取操作日志列表分页")
  @GetMapping("/page")
  public HttpResult<?> getPage(@PageBody PageableRequest<SysLogEntity> page) {
    return HttpResult.succeed(logService.getPage(page));
  }

  @ApiOperation("获取机构的操作日志列表")
  @GetMapping("/list")
  public HttpResult<?> getList(@GetBody SysLogEntity condition) {
    condition.setOrgId(StringUtils.isNotBlank(condition.getOrgId()) ? condition.getOrgId() : JwtTokenManager.currentOrgId());
    if (StringUtils.isBlank(condition.getOrgId())) {
      return HttpResult.fail("orgId为空");
    }
    return HttpResult.succeed(logService.getList(condition, null, null));
  }

}
