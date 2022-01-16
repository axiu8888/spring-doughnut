package com.benefitj.system.controller;

import com.benefitj.system.model.SysMenuEntity;
import com.benefitj.system.service.SysMenuService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单
 */
@AopWebPointCut
@Api(tags = {"菜单"}, description = "对菜单的各种操作")
@RestController
@RequestMapping("/menus")
public class MenuController {

  @Autowired
  private SysMenuService menuService;

  @ApiOperation("获取菜单")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "菜单ID", required = true, dataType = "String", dataTypeClass = String.class),
  })
  @GetMapping
  public HttpResult<?> get(String id) {
    SysMenuEntity menu = menuService.getById(id);
    return HttpResult.succeed(menu);
  }

  @ApiOperation("添加菜单")
  @PostMapping
  public HttpResult<?> create(SysMenuEntity menu) {
    menu = menuService.create(menu);
    return HttpResult.succeed(menu);
  }

  @ApiOperation("更新菜单")
  @PutMapping
  public HttpResult<?> update(@RequestBody SysMenuEntity menu) {
    if (StringUtils.isAnyBlank(menu.getId(), menu.getName())) {
      return HttpResult.fail("菜单ID和菜单名都不能为空");
    }
    menu = menuService.update(menu);
    return HttpResult.succeed(menu);
  }

  @ApiOperation("删除菜单")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "菜单ID", dataType = "String", dataTypeClass = String.class),
  })
  @DeleteMapping
  public HttpResult<?> delete(String id) {
    return HttpResult.succeed(menuService.delete(id));
  }

  @ApiOperation("改变菜单的状态")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "菜单ID", dataType = "String", paramType = "form", dataTypeClass = String.class),
      @ApiImplicitParam(name = "active", value = "状态", dataType = "Boolean", paramType = "form", dataTypeClass = Boolean.class),
  })
  @PatchMapping("/active")
  public HttpResult<?> changeActive(String id, Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("菜单ID不能为空");
    }
    return HttpResult.succeed(menuService.changeActive(id, active));
  }

  @ApiOperation("获取菜单列表分页")
  @GetMapping("/page")
  public HttpResult<?> getPage(@PageBody PageableRequest<SysMenuEntity> page) {
    return HttpResult.succeed(menuService.getPage(page));
  }

  @ApiOperation("获取机构的菜单列表")
  @GetMapping("/list")
  public HttpResult<?> getList(@GetBody SysMenuEntity condition) {
    condition.setOrgId(StringUtils.isNotBlank(condition.getOrgId()) ? condition.getOrgId() : JwtTokenManager.currentOrgId());
    if (StringUtils.isBlank(condition.getOrgId())) {
      return HttpResult.fail("orgId为空");
    }
    List<SysMenuEntity> menuList = menuService.getList(condition, null, null);
    return HttpResult.succeed(menuList);
  }

}
