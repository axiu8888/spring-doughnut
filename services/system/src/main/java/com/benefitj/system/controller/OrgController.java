package com.benefitj.system.controller;


import com.benefitj.system.model.SysOrgEntity;
import com.benefitj.system.service.SysOrgService;
import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.get.GetBody;
import com.benefitj.spring.mvc.page.PageBody;
import com.benefitj.spring.mvc.page.PageableRequest;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "机构ID", required = true, dataType = "String", dataTypeClass = String.class),
  })
  @GetMapping
  public HttpResult<?> get(String id) {
    SysOrgEntity org = orgService.get(id);
    return HttpResult.succeed(org);
  }

  @ApiOperation("添加机构")
  @PostMapping
  public HttpResult<?> create(SysOrgEntity org) {
    org = orgService.create(org);
    return HttpResult.succeed(org);
  }

  @ApiOperation("更新机构")
  @PutMapping
  public HttpResult<?> update(@RequestBody SysOrgEntity org) {
    if (StringUtils.isAnyBlank(org.getId(), org.getName())) {
      return HttpResult.fail("机构ID和机构名不能为空");
    }
    org = orgService.update(org);
    return HttpResult.succeed(org);
  }

  @ApiOperation("删除机构")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "机构ID", dataType = "String", dataTypeClass = String.class),
  })
  @DeleteMapping
  public HttpResult<?> delete(String id) {
    int count = orgService.deleteById(id);
    return HttpResult.succeed(count);
  }

  @ApiOperation("改变机构的状态")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "机构ID", dataType = "String", paramType = "form", dataTypeClass = String.class),
      @ApiImplicitParam(name = "active", value = "状态", dataType = "Boolean", paramType = "form", dataTypeClass = Boolean.class),
  })
  @PatchMapping("/active")
  public HttpResult<?> changeActive(String id, Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.succeed();
    }
    Boolean result = orgService.changeActive(id, active);
    return HttpResult.succeed(result);
  }

  @ApiOperation("获取子机构列表分页")
  @GetMapping("/page")
  public HttpResult<?> getPage(@PageBody PageableRequest<SysOrgEntity> page) {
    PageInfo<SysOrgEntity> orgList = orgService.getPage(page);
    return HttpResult.succeed(orgList);
  }

  @ApiOperation("获取机构列表")
  @GetMapping("/list")
  public HttpResult<?> getList(@GetBody SysOrgEntity condition) {
    condition.setId(StringUtils.isNotBlank(condition.getId()) ? condition.getId() : JwtTokenManager.currentOrgId());
    List<SysOrgEntity> organizations = orgService.getList(condition, null, null);
    return HttpResult.succeed(organizations);
  }

  @ApiOperation("获取子机构")
  @GetMapping("/children")
  public HttpResult<?> getChildren(@GetBody SysOrgEntity condition) {
    condition.setId(StringUtils.isNotBlank(condition.getId()) ? condition.getId() : JwtTokenManager.currentOrgId());
    condition.setId(null);
    List<SysOrgEntity> organizations = orgService.getList(condition, null, null);
    return HttpResult.succeed(organizations);
  }

  @ApiOperation("获取组织机构树")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "机构ID", dataType = "String", dataTypeClass = String.class),
      @ApiImplicitParam(name = "active", value = "是否可用", dataType = "Boolean", dataTypeClass = Boolean.class),
  })
  @GetMapping("/tree")
  public HttpResult<?> getOrgTree(String id, Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("机构id不能为空");
    }
    List<SysOrgEntity> list = orgService.getOrgTreeList(id, active);
    return HttpResult.succeed(list);
  }

}
