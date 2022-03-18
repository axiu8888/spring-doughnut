package com.benefitj.system.controller;

import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.query.PageBody;
import com.benefitj.spring.mvc.query.PageRequest;
import com.benefitj.spring.mvc.query.QueryBody;
import com.benefitj.spring.mvc.query.QueryRequest;
import com.benefitj.system.model.SysRoleEntity;
import com.benefitj.system.service.SysRoleService;
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
 * 角色
 */
@AopWebPointCut
@Api(tags = {"角色"}, description = "对角色的各种操作")
@RestController
@RequestMapping("/roles")
public class RoleController {

  @Autowired
  private SysRoleService roleService;

  @ApiOperation("获取角色")
  @GetMapping
  public HttpResult<SysRoleEntity> get(@ApiParam("角色ID") String id) {
    return HttpResult.succeed(roleService.getById(id));
  }

  @ApiOperation("添加角色")
  @PostMapping
  public HttpResult<SysRoleEntity> create(SysRoleEntity role) {
    return HttpResult.succeed(roleService.create(role));
  }

  @ApiOperation("更新角色")
  @PutMapping
  public HttpResult<?> update(@RequestBody SysRoleEntity role) {
    if (StringUtils.isAnyBlank(role.getId(), role.getName())) {
      return HttpResult.fail("角色ID和角色名都不能为空");
    }
    roleService.updateById(role);
    return HttpResult.succeed();
  }

  @ApiOperation("删除角色")
  @DeleteMapping
  public HttpResult<Integer> delete(@ApiParam("角色ID") String id) {
    return HttpResult.succeed(roleService.deleteById(id));
  }

  @ApiOperation("改变角色的状态")
  @PatchMapping("/active")
  public HttpResult<Boolean> changeActive(@ApiParam("角色ID") String id, @ApiParam("状态") Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("角色ID不能为空");
    }
    return HttpResult.succeed(roleService.changeActive(id, active));
  }

  @ApiOperation("获取角色列表分页")
  @GetMapping("/page")
  public HttpResult<PageInfo<SysRoleEntity>> getPage(@PageBody PageRequest<SysRoleEntity> request) {
    Utils.setOrgId(request.getCondition());
    return HttpResult.succeed(roleService.getPage(request));
  }

  @ApiOperation("获取机构的角色列表")
  @GetMapping("/list")
  public HttpResult<List<SysRoleEntity>> getList(@QueryBody QueryRequest<SysRoleEntity> request) {
    Utils.setOrgId(request.getCondition());
    return HttpResult.succeed(roleService.getList(request));
  }

}
