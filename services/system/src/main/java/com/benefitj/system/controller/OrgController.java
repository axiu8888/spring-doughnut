package com.benefitj.system.controller;


import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.query.PageBody;
import com.benefitj.spring.mvc.query.PageRequest;
import com.benefitj.spring.mvc.query.QueryBody;
import com.benefitj.spring.mvc.query.QueryRequest;
import com.benefitj.system.model.SysOrgEntity;
import com.benefitj.system.service.SysOrgService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机构
 */
@Api(tags = {"机构"}, description = "对机构的各种操作")
@AopWebPointCut
@RestController
@RequestMapping("/orgs")
public class OrgController {

  @Autowired
  private SysOrgService orgService;

  @ApiOperation("获取机构")
  @GetMapping
  public HttpResult<SysOrgEntity> get(@ApiParam("机构ID") String id) {
    return HttpResult.succeed(orgService.get(id));
  }

  @ApiOperation("添加机构")
  @PostMapping
  public HttpResult<SysOrgEntity> create(SysOrgEntity org) {
    return HttpResult.succeed(orgService.create(org));
  }

  @ApiOperation("更新机构")
  @PutMapping
  public HttpResult<?> update(@RequestBody SysOrgEntity org) {
    if (StringUtils.isAnyBlank(org.getId(), org.getName())) {
      return HttpResult.fail("机构ID和机构名不能为空");
    }
    orgService.update(org);
    return HttpResult.succeed();
  }

  @ApiOperation("删除机构")
  @DeleteMapping
  public HttpResult<Integer> delete(@ApiParam("机构ID") String id) {
    return HttpResult.succeed(orgService.deleteById(id));
  }

  @ApiOperation("改变机构的状态")
  @PatchMapping("/active")
  public HttpResult<Boolean> changeActive(@ApiParam("机构ID") String id, @ApiParam("状态") Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.succeed();
    }
    return HttpResult.succeed(orgService.changeActive(id, active));
  }

  @ApiOperation("获取子机构列表分页")
  @GetMapping("/page")
  public HttpResult<PageInfo<SysOrgEntity>> getPage(@PageBody PageRequest<SysOrgEntity> request) {
    setOrgId(request.getCondition());
    return HttpResult.succeed(orgService.getPage(request));
  }

  @ApiOperation("获取机构列表")
  @GetMapping("/list")
  public HttpResult<List<SysOrgEntity>> getList(@QueryBody QueryRequest<SysOrgEntity> request) {
    setOrgId(request.getCondition());
    return HttpResult.succeed(orgService.getList(request));
  }

  @ApiOperation("获取子机构")
  @GetMapping("/children")
  public HttpResult<List<SysOrgEntity>> getChildren(@QueryBody QueryRequest<SysOrgEntity> request) {
    setOrgId(request.getCondition());
    return HttpResult.succeed(orgService.getList(request));
  }

  @ApiOperation("获取组织机构树")
  @GetMapping("/tree")
  public HttpResult<List<SysOrgEntity>> getOrgTree(@ApiParam("机构ID") String id, @ApiParam("是否可用") Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("机构id不能为空");
    }
    List<SysOrgEntity> list = orgService.getOrgTreeList(id, active);
    return HttpResult.succeed(list);
  }

  private void setOrgId(SysOrgEntity condition) {
    condition.setPid(StringUtils.isNotBlank(condition.getPid())
        ? condition.getPid() : JwtTokenManager.currentOrgId());
  }

}
