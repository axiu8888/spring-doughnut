package com.benefitj.system.controller;


import com.benefitj.scaffold.http.HttpResult;
import com.benefitj.scaffold.security.token.JwtTokenManager;
import com.benefitj.spring.aop.web.AopWebPointCut;
import com.benefitj.spring.mvc.query.PageBody;
import com.benefitj.spring.mvc.query.PageRequest;
import com.benefitj.spring.mvc.query.QueryBody;
import com.benefitj.spring.mvc.query.QueryRequest;
import com.benefitj.system.model.SysUserEntity;
import com.benefitj.system.service.SysUserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
  @GetMapping
  public HttpResult<SysUserEntity> get(@ApiParam("用户ID") String id) {
    if (StringUtils.isBlank(id)) {
      return HttpResult.succeed();
    }
    return HttpResult.succeed(userService.getById(id));
  }

  @ApiOperation("更新用户信息")
  @PutMapping
  public HttpResult<?> update(@RequestBody SysUserEntity user) {
    if (StringUtils.isBlank(user.getId())) {
      return HttpResult.fail("用户ID不能为空");
    }
    userService.save(user);
    return HttpResult.succeed();
  }

  @ApiOperation("获取用户列表")
  @GetMapping("/list")
  public HttpResult<List<SysUserEntity>> getList(@QueryBody QueryRequest<SysUserEntity> request) {
    SysUserEntity condition = request.getCondition();
    condition.setOrgId(StringUtils.isNotBlank(condition.getOrgId())
        ? condition.getOrgId() : JwtTokenManager.currentOrgId());
    return HttpResult.succeed(userService.getList(request));
  }

  @ApiOperation("获取用户列表分页")
  @GetMapping("/page")
  public HttpResult<PageInfo<SysUserEntity>> getPage(@PageBody PageRequest<SysUserEntity> request) {
    return HttpResult.succeed(userService.getPage(request));
  }

}
