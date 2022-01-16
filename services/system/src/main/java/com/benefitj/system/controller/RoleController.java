package com.benefitj.system.controller;

import com.benefitj.system.model.SysRoleEntity;
import com.benefitj.system.service.SysRoleService;
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
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "角色ID", required = true, dataType = "String", dataTypeClass = String.class),
  })
  @GetMapping
  public HttpResult<?> get(String id) {
    return HttpResult.succeed(roleService.getById(id));
  }

  @ApiOperation("添加角色")
  @PostMapping
  public HttpResult<?> create(SysRoleEntity role) {
    return HttpResult.succeed(roleService.create(role));
  }

  @ApiOperation("更新角色")
  @PutMapping
  public HttpResult<?> update(@RequestBody SysRoleEntity role) {
    if (StringUtils.isAnyBlank(role.getId(), role.getName())) {
      return HttpResult.fail("角色ID和角色名都不能为空");
    }
    roleService.updateById(role);
    return HttpResult.succeed(role);
  }

  @ApiOperation("删除角色")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "角色ID", dataType = "String", dataTypeClass = String.class),
  })
  @DeleteMapping
  public HttpResult<?> delete(String id) {
    int count = roleService.deleteById(id);
    return HttpResult.succeed(count);
  }

  @ApiOperation("改变角色的状态")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "角色ID", dataType = "String", paramType = "form", dataTypeClass = String.class),
      @ApiImplicitParam(name = "active", value = "状态", dataType = "Boolean", paramType = "form", dataTypeClass = Boolean.class),
  })
  @PatchMapping("/active")
  public HttpResult<?> changeActive(String id, Boolean active) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.fail("角色ID不能为空");
    }
    Boolean result = roleService.changeActive(id, active);
    return HttpResult.succeed(result);
  }

  @ApiOperation("获取角色列表分页")
  @GetMapping("/page")
  public HttpResult<?> getPage(@PageBody PageableRequest<SysRoleEntity> page) {
    PageInfo<SysRoleEntity> roleList = roleService.getPage(page);
    return HttpResult.succeed(roleList);
  }

  @ApiOperation("获取机构的角色列表")
  @GetMapping("/list")
  public HttpResult<?> getList(@GetBody SysRoleEntity condition) {
    condition.setOrgId(StringUtils.isNotBlank(condition.getOrgId()) ? condition.getOrgId() : JwtTokenManager.currentOrgId());
    if (StringUtils.isBlank(condition.getOrgId())) {
      return HttpResult.fail("orgId为空");
    }
    List<SysRoleEntity> roleList = roleService.getList(condition, null, null);
    return HttpResult.succeed(roleList);
  }

}
