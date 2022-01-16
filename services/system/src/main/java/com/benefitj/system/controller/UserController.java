package com.benefitj.system.controller;


import com.benefitj.system.model.SysUserEntity;
import com.benefitj.system.service.SysUserService;
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
 * 用户
 */
@AopWebPointCut
@Api(tags = {"用户"}, description = "对用户的各种操作")
@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private SysUserService userService;

  @ApiOperation("获取用户信息")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String", dataTypeClass = String.class),
  })
  @GetMapping
  public HttpResult<?> get(String id) {
    if (StringUtils.isBlank(id)) {
      id = JwtTokenManager.currentUserId();
    }
    SysUserEntity userInfo = userService.getById(id);
    return HttpResult.succeed(userInfo);
  }

  @ApiOperation("更新用户信息")
  @PutMapping
  public HttpResult<?> update(@RequestBody SysUserEntity user) {
    if (StringUtils.isBlank(user.getId())) {
      return HttpResult.fail("用户ID不能为空");
    }
    userService.save(user);
    return HttpResult.succeed(user);
  }

  @ApiOperation("获取用户列表")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "orgId", value = "机构ID", dataType = "String", dataTypeClass = String.class),
      @ApiImplicitParam(name = "active", value = "是否可用", dataType = "Boolean", dataTypeClass = Boolean.class),
      @ApiImplicitParam(name = "gender", value = "性别", dataType = "Boolean", dataTypeClass = Boolean.class),
      @ApiImplicitParam(name = "multiLevel", value = "是否返回多级机构的数据", dataType = "Boolean", dataTypeClass = Boolean.class),
  })
  @GetMapping("/list")
  public HttpResult<?> getList(@GetBody SysUserEntity condition, Boolean multiLevel) {
    if (StringUtils.isNotBlank(condition.getOrgId())) {
      condition.setOrgId(JwtTokenManager.currentOrgId());
    }
    if (StringUtils.isBlank(condition.getOrgId())) {
      return HttpResult.fail("orgId为空");
    }
    List<SysUserEntity> users = userService.getList(condition, null, null);
    return HttpResult.succeed(users);
  }

  @ApiOperation("获取用户列表分页")
  @GetMapping("/page")
  public HttpResult<?> getPage(@PageBody PageableRequest<SysUserEntity> page) {
    return HttpResult.succeed(userService.getPage(page));
  }

}
