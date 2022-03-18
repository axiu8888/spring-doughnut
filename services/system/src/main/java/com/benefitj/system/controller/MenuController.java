package com.benefitj.system.controller;

import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.query.PageBody;
import com.benefitj.spring.mvc.query.PageRequest;
import com.benefitj.spring.mvc.query.QueryBody;
import com.benefitj.spring.mvc.query.QueryRequest;
import com.benefitj.system.model.SysMenuEntity;
import com.benefitj.system.service.SysMenuService;
import com.benefitj.system.utils.Utils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
  @GetMapping
  public HttpResult<SysMenuEntity> get(@ApiParam("菜单ID") String id) {
    SysMenuEntity menu = menuService.getById(id);
    return HttpResult.succeed(menu);
  }

  @ApiOperation("添加菜单")
  @PostMapping
  public HttpResult<SysMenuEntity> create(SysMenuEntity menu) {
    return HttpResult.succeed(menuService.create(menu));
  }

  @ApiOperation("更新菜单")
  @PutMapping
  public HttpResult<?> update(@RequestBody SysMenuEntity menu) {
    if (StringUtils.isAnyBlank(menu.getId(), menu.getName())) {
      return HttpResult.fail("菜单ID和菜单名都不能为空");
    }
    menuService.update(menu);
    return HttpResult.succeed();
  }

  @ApiOperation("删除菜单")
  @DeleteMapping
  public HttpResult<?> delete(@ApiParam("菜单ID") String id) {
    return HttpResult.succeed(menuService.delete(id));
  }

  @ApiOperation("改变菜单的状态")
  @PatchMapping("/active")
  public HttpResult<?> changeActive(@ApiParam("菜单ID") String id, @ApiParam("状态") Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("菜单ID不能为空");
    }
    return HttpResult.succeed(menuService.changeActive(id, active));
  }

  @ApiOperation("获取菜单列表分页")
  @GetMapping("/page")
  public HttpResult<PageInfo<SysMenuEntity>> getPage(@PageBody PageRequest<SysMenuEntity> request) {
    Utils.setOrgId(request.getCondition());
    return HttpResult.succeed(menuService.getPage(request));
  }

  @ApiOperation("获取机构的菜单列表")
  @GetMapping("/list")
  public HttpResult<List<SysMenuEntity>> getList(@QueryBody QueryRequest<SysMenuEntity> request) {
    Utils.setOrgId(request.getCondition());
    return HttpResult.succeed(menuService.getList(request));
  }

}
